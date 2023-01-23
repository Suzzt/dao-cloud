package com.junmo.boot.bootstrap;

import com.junmo.boot.annotation.DaoService;
import com.junmo.boot.handler.RpcRequestMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.DaoCallback;
import com.junmo.core.model.PingPongModel;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import com.junmo.core.util.SystemUtil;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sucf
 * @date 2022/12/29 16:30
 * @description:
 */
@Slf4j
public class RpcServerBootstrap implements ApplicationContextAware, InitializingBean, DisposableBean {

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

    private boolean isServerStarted;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        //scan annotation DaoService
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            return;
        }
        isServerStarted = true;
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
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
        DaoCloudProperties.serializerType = SerializeStrategyFactory.getSerializeType(DaoCloudProperties.serializer);
        if (!(DaoCloudProperties.corePoolSize > 0 && DaoCloudProperties.maxPoolSize > 0 && DaoCloudProperties.maxPoolSize >= DaoCloudProperties.corePoolSize)) {
            DaoCloudProperties.corePoolSize = 60;
            DaoCloudProperties.maxPoolSize = 300;
        }

        if (DaoCloudProperties.serverPort <= 0) {
            DaoCloudProperties.serverPort = SystemUtil.getAvailablePort(65535);
        }

        if (!StringUtils.hasLength(DaoCloudProperties.proxy)) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }

        // serializer instance
        //serializer = daoCloudProperties.getSerializer().newInstance();

        // build start callback
        startCallback = () -> {
            //register service
            RegistryManager.registry(DaoCloudProperties.proxy, InetAddress.getLocalHost().getHostAddress() + ":" + DaoCloudProperties.serverPort);
        };
    }

    /**
     * start
     */
    public void start() throws Exception {
        if (!isServerStarted) {
            return;
        }
        prepare();
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", DaoCloudProperties.corePoolSize, DaoCloudProperties.maxPoolSize);
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler(threadPoolProvider, this);
        thread = new Thread(() -> {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup(4);
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.group(boss, worker);
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new DaoMessageCoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                Channel channel = ctx.channel();
                                ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
                                    while (true) {
                                        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.PING_HEART_BEAT_MESSAGE, DaoCloudProperties.serializerType, new PingPongModel());
                                        channel.writeAndFlush(daoMessage);
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            log.error("<<<<<<<<<<< thread interrupted... >>>>>>>>>>", e);
                                        }
                                    }
                                });
                                super.channelRegistered(ctx);
                            }
                        });
                        ch.pipeline().addLast(rpcRequestMessageHandler);
                    }
                });
                Channel channel = serverBootstrap.bind(DaoCloudProperties.serverPort).sync().channel();
                log.debug(">>>>>>>>>>> start server port = {} bingo <<<<<<<<<<", DaoCloudProperties.serverPort);
                startCallback.run();
                channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("<<<<<<<<<<< start dao server interrupted error >>>>>>>>>>>");
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
