package com.junmo.boot.registry;

import com.junmo.boot.annotation.DaoService;
import com.junmo.boot.handler.RpcRequestMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.boot.serializer.Serializer;
import com.junmo.common.util.SystemUtil;
import com.junmo.common.util.ThreadPoolFactory;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.DaoCallback;
import com.junmo.core.model.PingPongModel;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: sucf
 * @date: 2022/12/29 16:30
 * @description:
 */
@Slf4j
public class ServerManager implements ApplicationContextAware, InitializingBean, DisposableBean {

    @Resource
    private DaoCloudProperties daoCloudProperties;

    private Serializer serializer;

    private Map<String, Object> localServiceCache = new HashMap<>();

    /**
     * do invoke when server start
     */
    private DaoCallback startCallback;
    /**
     * do invoke when server stop
     */
    private DaoCallback stopCallback;

    private Thread thread;

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        //scan annotation DaoService
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
            //DaoService daoService = serviceBean.getClass().getAnnotation(DaoService.class);
            String interfaces = serviceBean.getClass().getInterfaces()[0].getName();
            addServiceCache(interfaces, serviceBean);
        }
    }

    /**
     * prepare
     *
     * @throws Exception
     */
    public void prepare() throws Exception {
        if (daoCloudProperties.getSerializer() == null) {
            daoCloudProperties.setSerializer(null);
        }

        if (!(daoCloudProperties.getCorePoolSize() > 0 && daoCloudProperties.getMaxPoolSize() > 0 && daoCloudProperties.getMaxPoolSize() >= daoCloudProperties.getCorePoolSize())) {
            daoCloudProperties.setCorePoolSize(60);
            daoCloudProperties.setMaxPoolSize(300);
        }

        if (daoCloudProperties.getPort() <= 0) {
            daoCloudProperties.setPort(SystemUtil.getAvailablePort(65535));
        }

        if (!StringUtils.hasLength(daoCloudProperties.getProxy())) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }

        // serializer instance
        //serializer = daoCloudProperties.getSerializer().newInstance();

        // build start callback
        startCallback = () -> {
            //register service
            RegistryManager.registry(daoCloudProperties.getProxy(), InetAddress.getLocalHost().getHostAddress() + ":" + daoCloudProperties.getPort());

        };
    }

    /**
     * start
     */
    public void start() throws Exception {
        prepare();

        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", daoCloudProperties.getCorePoolSize(), daoCloudProperties.getMaxPoolSize());
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler(threadPoolProvider, this);
        thread = new Thread(() -> {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup(4);
            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.group(boss, worker);
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(new DaoMessageCoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                Channel channel = ctx.channel();
                                ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
                                    while (true) {
                                        channel.writeAndFlush(new PingPongModel());
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            log.debug("<<<<<<<<<<<thread interrupted...>>>>>>>>>>", e);
                                        }
                                    }
                                });
                                super.channelRegistered(ctx);
                            }
                        });
                        ch.pipeline().addLast(rpcRequestMessageHandler);
                    }
                });
                Channel channel = serverBootstrap.bind(daoCloudProperties.getPort()).sync().channel();
                log.debug("======✓✓✓✓✓✓start server .port = {} bingo✓✓✓✓✓✓======", daoCloudProperties.getPort());
                startCallback.run();
                channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("start dao server interrupted error", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * add server
     *
     * @param interfaces
     * @param serviceBean
     */
    private void addServiceCache(String interfaces, Object serviceBean) {
        localServiceCache.put(interfaces, serviceBean);
    }

    /**
     * invoke method
     *
     * @param requestModel
     * @return
     */
    public RpcResponseModel doInvoke(RpcRequestModel requestModel) {
        //  make response
        RpcResponseModel responseModel = new RpcResponseModel();
        responseModel.setSequenceId(requestModel.getSequenceId());

        // match service bean
        Object serviceBean = localServiceCache.get(requestModel.getInterfaceName());

        // valid
        if (serviceBean == null) {
            responseModel.setExceptionValue(new DaoException("no method exists"));
            return responseModel;
        }

        try {
            // invoke method
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = requestModel.getMethodName();
            Class<?>[] parameterTypes = requestModel.getParameterTypes();
            Object[] parameters = requestModel.getParameterValue();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            responseModel.setReturnValue(result);
        } catch (Throwable t) {
            log.error("dao-cloud provider invokeService error.", t);
            responseModel.setExceptionValue(new DaoException(t));
        }

        return responseModel;
    }

    @Override
    public void destroy() throws Exception {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        stopCallback.run();
        log.debug(">>>>>>>>>>> dao-cloud-rpc provider server destroy <<<<<<<<<<<<");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
