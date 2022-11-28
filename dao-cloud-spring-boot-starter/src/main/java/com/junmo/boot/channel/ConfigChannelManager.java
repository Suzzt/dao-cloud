package com.junmo.boot.channel;

import com.junmo.boot.registry.ConfigResponseMessageHandler;
import com.junmo.boot.registry.ServerRegistry;
import com.junmo.core.protocol.DefaultMessageCoder;
import com.junmo.core.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/11/22 23:37
 * @description:
 */
@Slf4j
public class ConfigChannelManager {
    private static volatile Channel configChannel;
    private static Object LOCK = new Object();

    public static Channel getChannel() {
        if (!ServerRegistry.HEART_BEAT) {
            invalid();
        }
        if (configChannel != null) {
            return configChannel;
        }
        synchronized (LOCK) {
            if (configChannel != null) {
                return configChannel;
            }
            initChannel();
            return configChannel;
        }
    }

    /**
     * configChannel >>> null
     */
    public static void invalid() {
        configChannel = null;
    }

    /**
     * init config channel
     */
    public static void initChannel() {
        log.info("======init config channel======");
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(LOGGING_HANDLER)
                        .addLast(new DefaultMessageCoder())
                        .addLast(new ConfigResponseMessageHandler());
            }
        });
        try {
            Channel channel = bootstrap.connect("dao.cloud.config.com", 5551).sync().channel();
            configChannel = channel;
            log.info("======✓✓✓✓✓✓init config channel bingo✓✓✓✓✓✓======");
        } catch (Exception e) {
            group.shutdownGracefully();
            log.error("connect config center error", e);
        }
    }
}
