package com.junmo.config.register;

import com.junmo.common.util.ThreadPoolFactory;
import com.junmo.config.register.handler.ServerRegisterMessageHandler;
import com.junmo.core.netty.protocol.DefaultMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2022/11/13 23:14
 * @description: 配置中心
 */
@Component
@Slf4j
public class RegisterConfig {
    private final int port = 5551;

    @PostConstruct
    public void start() {
        ThreadPoolFactory.GLOBAL_THREAD_POOL.submit(() -> {
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
                        ch.pipeline().addLast(new IdleStateHandler(3, 3, 3, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ServerRegisterMessageHandler());
                    }
                });
                Channel channel = serverBootstrap.bind(port).sync().channel();
                log.info(">>>>>>>>>>>>register-server success<<<<<<<<<<<");
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("server interrupted error", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        });
    }
}
