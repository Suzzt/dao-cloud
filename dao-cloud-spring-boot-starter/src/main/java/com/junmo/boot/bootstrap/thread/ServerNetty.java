package com.junmo.boot.bootstrap.thread;

import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.boot.bootstrap.RpcServerBootstrap;
import com.junmo.boot.handler.RpcRequestMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.model.PingPongModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
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

import java.net.InetAddress;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: sucf
 * @date: 2023/1/24 16:04
 * @description:
 */
@Slf4j
public class ServerNetty extends Thread{

    private ThreadPoolExecutor threadPoolProvider;

    private RpcServerBootstrap rpcServerBootstrap;

    public ServerNetty(ThreadPoolExecutor threadPoolProvider, RpcServerBootstrap rpcServerBootstrap) {
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
                    ch.pipeline().addLast(new RpcRequestMessageHandler(threadPoolProvider, rpcServerBootstrap));
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
