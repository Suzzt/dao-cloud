package com.dao.cloud.starter.bootstrap.thread;

import com.dao.cloud.starter.bootstrap.manager.RegistryManager;
import com.dao.cloud.starter.handler.GatewayServiceMessageHandler;
import com.dao.cloud.starter.handler.NettyGlobalTriggerExceptionHandler;
import com.dao.cloud.starter.properties.DaoCloudServerProperties;
import com.google.common.collect.Sets;
import com.dao.cloud.starter.bootstrap.manager.ServiceManager;
import com.dao.cloud.starter.handler.RpcServerMessageHandler;
import com.dao.cloud.starter.handler.ServerPingPongMessageHandler;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.ProtocolFrameDecoder;
import com.dao.cloud.core.resolver.MethodArgumentResolverHandler;
import com.dao.cloud.core.util.NetUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/1/24 16:04
 * @description:
 */
@Slf4j
public class Server {

    private ThreadPoolExecutor threadPoolProvider;
    private MethodArgumentResolverHandler methodArgumentResolverHandler = MethodArgumentResolverHandler.DEFAULT_RESOLVER;

    public Server(ThreadPoolExecutor threadPoolProvider) {
        this.threadPoolProvider = threadPoolProvider;
    }

    public Server(ThreadPoolExecutor threadPoolProvider, MethodArgumentResolverHandler methodArgumentResolverHandler) {
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
