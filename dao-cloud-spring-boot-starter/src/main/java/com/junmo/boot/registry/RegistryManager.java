package com.junmo.boot.registry;

import com.junmo.boot.handler.ConfigResponseMessageHandler;
import com.junmo.common.util.ThreadPoolFactory;
import com.junmo.core.enums.Constant;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ServerNode;
import com.junmo.core.model.ServerRegisterModel;
import com.junmo.core.netty.protocol.DaoMessage;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: sucf
 * @date: 2022/11/19 17:53
 * @description:
 */
@Slf4j
public class RegistryManager {
    private static volatile Channel registerChannel;
    private static Object LOCK = new Object();

    /**
     * get register channel
     *
     * @return
     */
    private static Channel getChannel() {
        if (registerChannel != null) {
            return registerChannel;
        }
        synchronized (LOCK) {
            if (registerChannel != null) {
                return registerChannel;
            }
            initChannel();
            return registerChannel;
        }
    }

    /**
     * init config channel
     */
    public static void initChannel() {
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
            registerChannel = bootstrap.connect("dao.cloud.config.com", 5551).sync().channel();
            log.info(">>>>>>>>> init register channel finish. <<<<<<<<<< :)bingo(:");
        } catch (Exception e) {
            group.shutdownGracefully();
            log.error("connect config center error", e);
        }
    }

    /**
     * get service node
     *
     * @param proxy
     * @return
     */
    public static List<ServerNode> getServiceNode(String proxy) {
//        DaoMessage daoMessage = new DaoMessage();
//        registerChannel.writeAndFlush();
        return null;
    }

    /**
     * 服务注册
     *
     * @param proxy
     * @param ipLinkPort
     */
    public static void registry(String proxy, String ipLinkPort) {
        ServerRegisterModel serverRegisterModel = new ServerRegisterModel();
        serverRegisterModel.setIpLinkPort(ipLinkPort);
        serverRegisterModel.setProxy(proxy);
        //heart
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
            while (true) {
                send(serverRegisterModel);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("thread interrupted...");
                }
            }
        });
    }

    /**
     * 发送注册请求
     *
     * @param serverRegisterModel
     */
    private static void send(ServerRegisterModel serverRegisterModel) throws DaoException {
        Channel channel = getChannel();
        if (channel == null) {
            throw new DaoException("connect config center error");
        }
        DaoMessage daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, serverRegisterModel);
        channel.writeAndFlush(daoMessage);
    }
}
