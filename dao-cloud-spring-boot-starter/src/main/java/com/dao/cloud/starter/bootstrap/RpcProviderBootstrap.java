package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.ProtocolFrameDecoder;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.resolver.MethodArgumentResolverHandler;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.SystemUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.starter.annotation.ConditionalOnUseAnnotation;
import com.dao.cloud.starter.annotation.DaoService;
import com.dao.cloud.starter.manager.RegistryManager;
import com.dao.cloud.starter.manager.ServiceManager;
import com.dao.cloud.starter.unit.ServiceInvoker;
import com.dao.cloud.starter.handler.GatewayServiceMessageHandler;
import com.dao.cloud.starter.handler.NettyGlobalTriggerExceptionHandler;
import com.dao.cloud.starter.handler.RpcServerMessageHandler;
import com.dao.cloud.starter.handler.ServerPingPongMessageHandler;
import com.dao.cloud.starter.properties.DaoCloudServerProperties;
import com.google.common.collect.Sets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @date 2022/12/29 16:30
 * @description: rpc provider startup
 */
@Slf4j
@ConditionalOnUseAnnotation(annotation = DaoService.class)
public class RpcProviderBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private MethodArgumentResolverHandler methodArgumentResolverHandler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            return;
        }
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
            DaoService daoService = serviceBean.getClass().getAnnotation(DaoService.class);
            String interfaces = serviceBean.getClass().getInterfaces()[0].getName();
            String provider = StringUtils.hasLength(daoService.provider()) ? daoService.provider() : interfaces;
            ServiceInvoker serviceInvoker = new ServiceInvoker(SerializeStrategyFactory.getSerializeType(daoService.serializable().getName()), serviceBean);
            ServiceManager.addService(provider, daoService.version(), serviceInvoker);
        }
        start();
    }

    /**
     * start server
     */
    public void start() {
        if (!(DaoCloudServerProperties.corePoolSize > 0 && DaoCloudServerProperties.maxPoolSize > 0 && DaoCloudServerProperties.maxPoolSize >= DaoCloudServerProperties.corePoolSize)) {
            DaoCloudServerProperties.corePoolSize = 60;
            DaoCloudServerProperties.maxPoolSize = 300;
        }

        if (DaoCloudServerProperties.serverPort <= 0) {
            try {
                DaoCloudServerProperties.serverPort = SystemUtil.getAvailablePort(65535);
            } catch (Exception e) {
                throw new DaoException(e);
            }
        }

        if (!StringUtils.hasLength(DaoCloudServerProperties.proxy)) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", DaoCloudServerProperties.corePoolSize, DaoCloudServerProperties.maxPoolSize);
        new NettyServer(threadPoolProvider, methodArgumentResolverHandler).start();
    }

    private static class NettyServer {

        private ThreadPoolExecutor threadPoolProvider;
        private MethodArgumentResolverHandler methodArgumentResolverHandler = MethodArgumentResolverHandler.DEFAULT_RESOLVER;

        public NettyServer(ThreadPoolExecutor threadPoolProvider) {
            this.threadPoolProvider = threadPoolProvider;
        }

        public NettyServer(ThreadPoolExecutor threadPoolProvider, MethodArgumentResolverHandler methodArgumentResolverHandler) {
            this(threadPoolProvider);
            this.methodArgumentResolverHandler = Objects.isNull(methodArgumentResolverHandler)
                    ? MethodArgumentResolverHandler.DEFAULT_RESOLVER
                    : methodArgumentResolverHandler;
        }

        public void start() {
            NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("rpc-server-boss", true));
            NioEventLoopGroup worker = new NioEventLoopGroup(4, new DefaultThreadFactory("rpc-server-worker", true));
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
                        ch.pipeline().addLast(new RpcServerMessageHandler(threadPoolProvider));
                        ch.pipeline().addLast(new NettyGlobalTriggerExceptionHandler());
                    }
                });
                serverBootstrap.bind(DaoCloudServerProperties.serverPort).sync();
                log.info(">>>>>>>>>>> start server port = {} bingo <<<<<<<<<<", DaoCloudServerProperties.serverPort);
                // register service
                RegisterProviderModel registerProviderModel = new RegisterProviderModel();
                registerProviderModel.setProxy(DaoCloudServerProperties.proxy);
                Set<ProviderModel> providerModels = ServiceManager.getServiceInvokers().keySet();
                registerProviderModel.setProviderModels(Sets.newHashSet(providerModels));
                registerProviderModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudServerProperties.serverPort));
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
