package com.junmo.boot.channel;

import com.junmo.boot.handler.RpcResponseMessageHandler;
import com.junmo.core.netty.protocol.DefaultMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/10/28 19:04
 * @description:
 */
@Slf4j
public class ClientChannelFactory {
    private static Map<String, Channel> mapChannel = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();

    /**
     * 获取channel
     *
     * @return
     */
    public static Channel getChannel(String ip, int port) {
        Channel channel = mapChannel.get(ip + "#" + port);
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            create(ip, port);
            return mapChannel.get(ip + "#" + port);
        }
    }

    private static void create(String ip, int port) {
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
                        .addLast(new RpcResponseMessageHandler());
                log.info(">>>>>>>>>>建立连接<<<<<<<<<<<<");
            }
        });
        try {
            Channel channel = bootstrap.connect(ip, port).sync().channel();
            mapChannel.put(ip + "#" + port, channel);
        } catch (Exception e) {
            log.error("init channel exception error", e);
        }
    }
}
