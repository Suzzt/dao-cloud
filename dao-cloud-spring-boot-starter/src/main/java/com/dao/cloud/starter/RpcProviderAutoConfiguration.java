package com.dao.cloud.starter;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.ProtocolFrameDecoder;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.resolver.MethodArgumentResolverHandler;
import com.dao.cloud.core.util.DaoTimer;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.SystemUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.starter.annotation.ConditionalOnUseAnnotation;
import com.dao.cloud.starter.annotation.DaoCallTrend;
import com.dao.cloud.starter.annotation.DaoService;
import com.dao.cloud.starter.handler.GatewayServiceMessageHandler;
import com.dao.cloud.starter.handler.NettyGlobalTriggerExceptionHandler;
import com.dao.cloud.starter.handler.RpcServerMessageHandler;
import com.dao.cloud.starter.handler.ServerPingPongMessageHandler;
import com.dao.cloud.starter.log.LogHandlerInterceptor;
import com.dao.cloud.starter.manager.RegistryManager;
import com.dao.cloud.starter.manager.ServiceManager;
import com.dao.cloud.starter.properties.DaoCloudProviderServiceProperties;
import com.dao.cloud.starter.unit.CallTrendServiceInvoker;
import com.dao.cloud.starter.unit.CallTrendTimerTask;
import com.dao.cloud.starter.unit.ServiceInvoker;
import com.dao.cloud.starter.utils.MethodUtils;
import com.google.common.collect.Sets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RPC (Provider) AutoConfiguration
 *
 * @author sucf
 * @date 2022/12/29 16:30
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "dao-cloud", name = "enable", havingValue = "true")
@ConditionalOnUseAnnotation(annotation = DaoService.class)
@EnableConfigurationProperties(DaoCloudProviderServiceProperties.class)
public class RpcProviderAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final DaoCloudProviderServiceProperties daoCloudProviderServiceProperties;

    private final MethodArgumentResolverHandler methodArgumentResolverHandler;

    public RpcProviderAutoConfiguration(DaoCloudProviderServiceProperties daoCloudProviderServiceProperties, MethodArgumentResolverHandler methodArgumentResolverHandler) {
        this.daoCloudProviderServiceProperties = daoCloudProviderServiceProperties;
        this.methodArgumentResolverHandler = methodArgumentResolverHandler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            return;
        }
        for (Map.Entry<String, Object> entry : serviceBeanMap.entrySet()) {
            Object serviceBean = entry.getValue();
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
            DaoService daoService = serviceBean.getClass().getAnnotation(DaoService.class);
            String interfaces = serviceBean.getClass().getInterfaces()[0].getName();
            String provider = StringUtils.hasLength(daoService.provider()) ? daoService.provider() : interfaces;
            Map<String, CallTrendTimerTask> interfacesCallTrendMap = new HashMap<>();
            ProxyProviderModel proxyProviderModel = new ProxyProviderModel(daoCloudProviderServiceProperties.getProxy(), provider, daoService.version());
            boolean flag = false;
            for (Method method : serviceBean.getClass().getDeclaredMethods()) {
                DaoCallTrend daoCallTrend = method.getAnnotation(DaoCallTrend.class);
                if (daoCallTrend != null) {
                    flag = true;
                    String methodName = MethodUtils.methodToString(method);
                    CallTrendTimerTask callTrendTimerTask = new CallTrendTimerTask(proxyProviderModel, methodName, daoCallTrend.interval(), daoCallTrend.time_unit());
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(callTrendTimerTask, daoCallTrend.interval(), daoCallTrend.time_unit());
                    interfacesCallTrendMap.put(methodName, callTrendTimerTask);
                }
            }
            ServiceInvoker serviceInvoker;
            if (flag) {
                serviceInvoker = new CallTrendServiceInvoker(SerializeStrategyFactory.getSerializeType(daoService.serializable().getName()), serviceBean, interfacesCallTrendMap);
            } else {
                serviceInvoker = new ServiceInvoker(SerializeStrategyFactory.getSerializeType(daoService.serializable().getName()), serviceBean);
            }
            ServiceManager.addService(provider, daoService.version(), serviceInvoker);
        }
        start();
    }

    /**
     * start server
     */
    public void start() {
        if (!(daoCloudProviderServiceProperties.getCorePoolSize() > 0 && daoCloudProviderServiceProperties.getMaxPoolSize() > 0 && daoCloudProviderServiceProperties.getMaxPoolSize() >= daoCloudProviderServiceProperties.getCorePoolSize())) {
            daoCloudProviderServiceProperties.setCorePoolSize(60);
            daoCloudProviderServiceProperties.setMaxPoolSize(300);
        }

        if (daoCloudProviderServiceProperties.getServerPort() <= 0) {
            try {
                daoCloudProviderServiceProperties.setServerPort(SystemUtil.getAvailablePort(65535));
            } catch (Exception e) {
                throw new DaoException(e);
            }
        }

        if (!StringUtils.hasLength(daoCloudProviderServiceProperties.getProxy())) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", daoCloudProviderServiceProperties.getCorePoolSize(), daoCloudProviderServiceProperties.getMaxPoolSize());
        new NettyServer(daoCloudProviderServiceProperties.getProxy(), daoCloudProviderServiceProperties.getServerPort(), threadPoolProvider, methodArgumentResolverHandler).start();
    }

    private static class NettyServer {

        private final String serverProxy;
        private final int serverPort;
        private final ThreadPoolExecutor threadPoolProvider;
        private final MethodArgumentResolverHandler methodArgumentResolverHandler;

        public NettyServer(String serverProxy, int serverPort, ThreadPoolExecutor threadPoolProvider, MethodArgumentResolverHandler methodArgumentResolverHandler) {
            this.threadPoolProvider = threadPoolProvider;
            this.serverProxy = serverProxy;
            this.serverPort = serverPort;
            this.methodArgumentResolverHandler = Objects.isNull(methodArgumentResolverHandler) ? MethodArgumentResolverHandler.DEFAULT_RESOLVER : methodArgumentResolverHandler;
        }

        public void start() {
            NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("rpc-server-boss", true));
            NioEventLoopGroup worker = new NioEventLoopGroup(4, new DefaultThreadFactory("rpc-server-worker", true));
            LogHandlerInterceptor logHandlerInterceptor = new LogHandlerInterceptor();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.group(boss, worker);
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new DaoMessageCoder());
                        ch.pipeline().addLast("serverIdleHandler", new IdleStateHandler(0, 0, 4, TimeUnit.SECONDS));
                        ch.pipeline().addLast("serverHeartbeatHandler", new ServerPingPongMessageHandler());
                        ch.pipeline().addLast(new GatewayServiceMessageHandler(methodArgumentResolverHandler));
                        ch.pipeline().addLast(new RpcServerMessageHandler(threadPoolProvider, logHandlerInterceptor));
                        ch.pipeline().addLast(new NettyGlobalTriggerExceptionHandler());
                    }
                });
                serverBootstrap.bind(serverPort).sync();
                log.info(">>>>>>>>>>> start server port = {} bingo <<<<<<<<<<", serverPort);
                // register service
                RegisterProviderModel registerProviderModel = new RegisterProviderModel();
                registerProviderModel.setProxy(serverProxy);
                Set<ProviderModel> providerModels = ServiceManager.getServiceInvokers().keySet();
                registerProviderModel.setProviderModels(Sets.newHashSet(providerModels));
                registerProviderModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), serverPort));
                RegistryManager.registry(registerProviderModel);
            } catch (Exception e) {
                log.error("<<<<<<<<<<< start dao server interrupted error >>>>>>>>>>>", e);
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                System.exit(1);
            }
        }
    }
}
