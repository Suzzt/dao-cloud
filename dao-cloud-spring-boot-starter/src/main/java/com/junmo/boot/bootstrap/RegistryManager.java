package com.junmo.boot.bootstrap;

import com.junmo.boot.handler.ConfigPollMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterModel;
import com.junmo.core.model.RegisterProxyModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.DaoTimer;
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
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2022/11/19 17:53
 * @description:
 */
@Slf4j
public class RegistryManager {

    private static volatile Channel REGISTER_CHANNEL;

    private static final Object LOCK = new Object();

    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private static int CONNECT_PORT = 5551;

    /**
     * get register channel
     *
     * @return
     */
    private static Channel getChannel() {
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
        String ip = NetUtil.getServerIP("dao.cloud.config.com");
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
                        .addLast(new ConfigPollMessageHandler());
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

    /**
     * poll register server node by center
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    public static List<ServerNodeModel> poll(String proxy, int version) throws Exception {
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_REQUEST_MESSAGE, DaoCloudProperties.serializerType, new RegisterProxyModel(proxy, version));
        DefaultPromise<List<ServerNodeModel>> promise = new DefaultPromise<>(getChannel().eventLoop());
        ConfigPollMessageHandler.PROMISE_MAP.put(proxy + "#" + version, promise);
        getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                promise.setFailure(future.cause());
            }
        });
        if (!promise.await(8, TimeUnit.SECONDS)) {
            throw new DaoException(promise.cause());
        }
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
        send(registerModel);
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    send(registerModel);
                } catch (Exception e) {
                    log.error("<<<<<<<<< send register server exception >>>>>>>>", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 5, TimeUnit.SECONDS);
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
                log.error("<<<<<<<<< send register server error >>>>>>>>", future.cause());
            }
        });
    }
}
