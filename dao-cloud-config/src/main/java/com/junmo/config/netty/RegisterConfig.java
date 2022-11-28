package com.junmo.config.netty;

import com.junmo.config.netty.handler.ServerRegisterMessageHandler;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: sucf
 * @date: 2022/11/13 23:14
 * @description: 配置中心
 */
@Component
@Slf4j
public class RegisterConfig {
    @PostConstruct
    public void start() {
        new Thread(new Runnable() {
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
                            ch.pipeline().addLast(new ServerRegisterMessageHandler());
                        }
                    });
                    Channel channel = serverBootstrap.bind(5551).sync().channel();
                    log.info(">>>>>>>>>>>>register-server<<<<<<<<<<<");
                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("server interrupted error", e);
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        }).start();
    }
}
