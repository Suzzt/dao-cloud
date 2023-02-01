package com.junmo.boot.bootstrap.thread;

import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.boot.bootstrap.RpcServerBootstrap;
import com.junmo.boot.handler.RpcServerMessageHandler;
import com.junmo.boot.handler.ServerPingPongMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
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

    private RpcServerBootstrap rpcServerBootstrap;

    public Server(ThreadPoolExecutor threadPoolProvider, RpcServerBootstrap rpcServerBootstrap) {
        this.threadPoolProvider = threadPoolProvider;
        this.rpcServerBootstrap = rpcServerBootstrap;
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
                    ch.pipeline().addLast(new RpcServerMessageHandler(threadPoolProvider, rpcServerBootstrap));
                }
            });
            Channel channel = serverBootstrap.bind(DaoCloudProperties.serverPort).sync().channel();
            log.debug(">>>>>>>>>>> start server port = {} bingo <<<<<<<<<<", DaoCloudProperties.serverPort);
            // register service
            RegistryManager.registry(DaoCloudProperties.proxy, InetAddress.getLocalHost().getHostAddress() + ":" + DaoCloudProperties.serverPort);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("<<<<<<<<<<< start dao server interrupted error >>>>>>>>>>>");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
