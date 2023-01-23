package com.junmo.boot.bootstrap;

import com.junmo.boot.handler.ConfigResponseMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterModel;
import com.junmo.core.model.RegisterPollModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.NetUtil;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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

    private static void destroy() {
        registerChannel = null;
    }

    /**
     * init config channel
     */
    public static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new ConfigResponseMessageHandler());
            }
        });
        try {
            String ip = NetUtil.getServerIP("dao.cloud.config.com");
            ip = StringUtils.hasLength(ip) ? ip : "127.0.0.1";
            registerChannel = bootstrap.connect(ip, 5551).sync().channel();
            log.info(">>>>>>>>> connect register channel success. <<<<<<<<<< :)bingo(:");
        } catch (Exception e) {
            group.shutdownGracefully();
            log.error("<<<<<<<<<< connect config center error >>>>>>>>>>", e);
        }
    }

    /**
     * poll register server node by center
     *
     * @param proxy
     * @return
     */
    public static List<ServerNodeModel> poll(String proxy) throws InterruptedException {
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_REQUEST_MESSAGE, DaoCloudProperties.serializerType, new RegisterPollModel(proxy));
        getChannel().writeAndFlush(daoMessage);
        DefaultPromise<List<ServerNodeModel>> promise = new DefaultPromise<>(getChannel().eventLoop());
        ConfigResponseMessageHandler.PROMISE_MAP.put(proxy, promise);
        promise.await();
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new DaoException(promise.cause());
        }
    }

    /**
     * 服务注册
     *
     * @param proxy
     * @param ipLinkPort
     */
    public static void registry(String proxy, String ipLinkPort) {
        RegisterModel registerModel = new RegisterModel();
        registerModel.setIpLinkPort(ipLinkPort);
        registerModel.setProxy(proxy);
        //heart
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(() -> {
            while (true) {
                try {
                    // TODO: 2023/1/19 这里要想办法判断下注册中心有没有返回
                    send(registerModel);
                } catch (DaoException e) {
                    log.error("<<<<<<<<<<< send register message disconnect >>>>>>>>>>", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error("<<<<<<<<<<< thread interrupted... >>>>>>>>>>", e);
                }
            }
        });
    }

    /**
     * 发送注册请求
     *
     * @param registerModel
     */
    private static void send(RegisterModel registerModel) throws DaoException {
        Channel channel = getChannel();
        if (channel == null) {
            throw new DaoException("connect config center error");
        }
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.REGISTRY_REQUEST_MESSAGE, DaoCloudProperties.serializerType, registerModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                destroy();
            }
        });
    }
}
