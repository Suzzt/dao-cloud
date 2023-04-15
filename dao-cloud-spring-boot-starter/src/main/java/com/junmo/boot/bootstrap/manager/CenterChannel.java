package com.junmo.boot.bootstrap.manager;

import com.junmo.boot.handler.CenterConfigMessageHandler;
import com.junmo.boot.handler.CenterServerMessageHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.NetUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/2/27 11:38
 * @description:
 */
@Slf4j
public class CenterChannel {
    private static volatile Channel REGISTER_CHANNEL;

    private static final Object LOCK = new Object();

    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private static int CONNECT_PORT = 5551;

    /**
     * get center channel
     *
     * @return
     */
    public static Channel getChannel() {
        if (REGISTER_CHANNEL != null) {
            return REGISTER_CHANNEL;
        }
        synchronized (LOCK) {
            if (REGISTER_CHANNEL != null) {
                return REGISTER_CHANNEL;
            }
            connect();
            return REGISTER_CHANNEL;
        }
    }

    /**
     * connect registry
     */
    public static void connect() {
        String ip = NetUtil.getServerIP(DaoCloudConstant.CENTER_HOST);
        ip = StringUtils.hasLength(ip) ? ip : NetUtil.getLocalIp();
        NioEventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP.channel(NioSocketChannel.class);
        BOOTSTRAP.remoteAddress(ip, CONNECT_PORT);
        BOOTSTRAP.group(group);
        BOOTSTRAP.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new IdleStateHandler(8, 0, 0, TimeUnit.SECONDS))
                        .addLast(new CenterConfigMessageHandler())
                        .addLast(new CenterServerMessageHandler());
            }
        });
        try {
            REGISTER_CHANNEL = BOOTSTRAP.connect().sync().channel();
            log.info(">>>>>>>>> connect register channel success. <<<<<<<<<< :)bingo(:");
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect config center error >>>>>>>>>>", e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public static void reconnect() {
        REGISTER_CHANNEL.close().addListener(future -> {
            REGISTER_CHANNEL.eventLoop().schedule(() -> {
                BOOTSTRAP.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            REGISTER_CHANNEL = future.channel();
                            log.info(">>>>>>>>> reconnect register channel success. <<<<<<<<<< :)bingo(:");
                        } else {
                            log.error("<<<<<<<<<< reconnect config center error >>>>>>>>>>", future.cause());
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        });
    }
}
