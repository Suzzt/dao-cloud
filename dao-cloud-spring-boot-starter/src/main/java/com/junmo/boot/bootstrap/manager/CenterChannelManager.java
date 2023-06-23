package com.junmo.boot.bootstrap.manager;

import com.junmo.boot.bootstrap.thread.InquireClusterTimer;
import com.junmo.boot.handler.CenterConfigMessageHandler;
import com.junmo.boot.handler.CenterServerMessageHandler;
import com.junmo.boot.handler.InquireClusterCenterResponseHandler;
import com.junmo.core.MainProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ClusterCenterNodeModel;
import com.junmo.core.model.ClusterInquireMarkModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/2/27 11:38
 * @description:
 */
@Slf4j
public class CenterChannelManager {

    private static String CURRENT_USE_CENTER_IP;

    private static Iterator<String> CLUSTER_CENTER_IP_ITERATOR;

    private static volatile Channel CONNECT_CENTER_CHANNEL;

    private static final Object LOCK = new Object();

    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private static int CONNECT_PORT = 5551;

    private static Thread timer;

    public static void init(String centerIp) throws InterruptedException {
        CURRENT_USE_CENTER_IP = centerIp;
        inquire();
        timer = new Thread(new InquireClusterTimer());
        timer.start();
    }

    /**
     * inquire cluster ips
     */
    public static void inquire() throws InterruptedException {
        ClusterInquireMarkModel clusterInquireMarkModel = new ClusterInquireMarkModel();
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, MainProperties.serialize, clusterInquireMarkModel);
        DefaultPromise<ClusterCenterNodeModel> promise = new DefaultPromise<>(getChannel().eventLoop());
        InquireClusterCenterResponseHandler.promise = promise;
        getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<< send inquire cluster node(ip={}) error >>>>>>>>>>>", future.cause(), CURRENT_USE_CENTER_IP);
            }
        });
        if (!promise.await(3, TimeUnit.SECONDS)) {
            throw new DaoException(promise.cause());
        }
        if (promise.isSuccess()) {
            CLUSTER_CENTER_IP_ITERATOR = promise.getNow().getClusterNodes().iterator();
        } else {
            throw new DaoException(promise.cause());
        }
    }

    /**
     * get center channel
     *
     * @return
     */
    public static Channel getChannel() {
        if (CONNECT_CENTER_CHANNEL != null) {
            return CONNECT_CENTER_CHANNEL;
        }
        synchronized (LOCK) {
            if (CONNECT_CENTER_CHANNEL != null) {
                return CONNECT_CENTER_CHANNEL;
            }
            connect();
            return CONNECT_CENTER_CHANNEL;
        }
    }

    /**
     * connect registry
     */
    public static void connect() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP.channel(NioSocketChannel.class);
        BOOTSTRAP.remoteAddress(CURRENT_USE_CENTER_IP, CONNECT_PORT);
        BOOTSTRAP.group(group);
        BOOTSTRAP.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new IdleStateHandler(8, 0, 0, TimeUnit.SECONDS))
                        .addLast(new InquireClusterCenterResponseHandler())
                        .addLast(new CenterConfigMessageHandler())
                        .addLast(new CenterServerMessageHandler());
            }
        });
        try {
            CONNECT_CENTER_CHANNEL = BOOTSTRAP.connect().sync().channel();
            log.info(">>>>>>>>> connect center node(ip={}) success. <<<<<<<<<< :)bingo(:", CURRENT_USE_CENTER_IP);
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect center node(ip={}) error >>>>>>>>>>", e, CURRENT_USE_CENTER_IP);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public static void reconnect() {
        CONNECT_CENTER_CHANNEL.close().addListener(future -> {
            CONNECT_CENTER_CHANNEL.eventLoop().schedule(() -> {
                BOOTSTRAP.remoteAddress(CURRENT_USE_CENTER_IP, CONNECT_PORT);
                BOOTSTRAP.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (future.isSuccess()) {
                            CONNECT_CENTER_CHANNEL = future.channel();
                            log.info(">>>>>>>>> reconnect center node(ip={}) success. <<<<<<<<<< :)bingo(:", CURRENT_USE_CENTER_IP);
                        } else {
                            shuffle();
                            log.error("<<<<<<<<<< reconnect center node(ip={}) error >>>>>>>>>>", future.cause());
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        });
    }

    /**
     * Filter to connect the cluster ip
     */
    private static void shuffle() {
        if (CLUSTER_CENTER_IP_ITERATOR.hasNext()) {
            CURRENT_USE_CENTER_IP = CLUSTER_CENTER_IP_ITERATOR.next();
        }
    }
}
