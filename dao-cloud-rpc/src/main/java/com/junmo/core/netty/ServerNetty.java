package com.junmo.core.netty;

import com.junmo.core.handler.RpcRequestMessageHandler;
import com.junmo.core.protocol.DefaultMessageCoder;
import com.junmo.core.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/11/27 00:10
 * @description:
 */
@Slf4j
public class ServerNetty implements Runnable{
    @Override
    public void run() {
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
                    ch.pipeline().addLast(new DefaultMessageCoder());
                    ch.pipeline().addLast(new RpcRequestMessageHandler());
                }
            });
            Channel channel = serverBootstrap.bind(6661).sync().channel();
            log.debug("======✓✓✓✓✓✓start server dao bingo✓✓✓✓✓✓======");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("start dao server interrupted error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
