package com.junmo.boot.bootstrap.thread;

import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.manager.RegistryManager;
import com.junmo.boot.bootstrap.manager.ServiceManager;
import com.junmo.boot.handler.RpcServerMessageHandler;
import com.junmo.boot.handler.ServerPingPongMessageHandler;
import com.junmo.boot.properties.DaoCloudServerProperties;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.NetUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
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
public class Server extends Thread {

    private ThreadPoolExecutor threadPoolProvider;

    public Server(ThreadPoolExecutor threadPoolProvider) {
        this.threadPoolProvider = threadPoolProvider;
    }

    @Override
    public void run() {
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
                    ch.pipeline().addLast("serverIdleHandler", new IdleStateHandler(0, 0, 4, TimeUnit.SECONDS));
                    ch.pipeline().addLast("serverHeartbeatHandler", new ServerPingPongMessageHandler());
                    ch.pipeline().addLast(new RpcServerMessageHandler(threadPoolProvider));
                }
            });
            Channel channel = serverBootstrap.bind(DaoCloudServerProperties.serverPort).sync().channel();
            log.info(">>>>>>>>>>> start server port = {} bingo <<<<<<<<<<", DaoCloudServerProperties.serverPort);
            // register service
            RegisterProviderModel registerProviderModel = new RegisterProviderModel();
            registerProviderModel.setProxy(DaoCloudServerProperties.proxy);
            Set<ProviderModel> providerModels = ServiceManager.getServiceInvokers().keySet();
            registerProviderModel.setProviderModels(Sets.newHashSet(providerModels));
            registerProviderModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudServerProperties.serverPort));
            RegistryManager.registry(registerProviderModel);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("<<<<<<<<<<< start dao server interrupted error >>>>>>>>>>>");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
