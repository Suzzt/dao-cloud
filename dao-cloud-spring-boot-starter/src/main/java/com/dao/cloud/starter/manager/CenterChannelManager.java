package com.dao.cloud.starter.manager;

import com.dao.cloud.starter.handler.*;
import com.dao.cloud.starter.timer.InquireClusterTimer;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ClusterCenterNodeModel;
import com.dao.cloud.core.model.ClusterInquireMarkModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.DaoMessageCoder;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.netty.protocol.VarIntsProtocolFrameDecoder;
import com.dao.cloud.core.util.DaoCloudConstant;
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
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/27 11:38
 */
@Slf4j
public class CenterChannelManager {

    private static String CURRENT_USE_CENTER_IP;

    private static Iterator<String> CLUSTER_CENTER_IP_ITERATOR;

    private static volatile Channel CONNECT_CENTER_CHANNEL;

    private static final Object LOCK = new Object();

    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private static final String DEFAULT_LOCAL_IP = "127.0.0.1";

    private static Thread timer;

    /**
     * init
     *
     * @param centerIp
     * @throws InterruptedException
     */
    public static void init(String centerIp) throws InterruptedException {
        CURRENT_USE_CENTER_IP = StringUtils.hasLength(centerIp) ? centerIp : DEFAULT_LOCAL_IP;
        inquire();
        timer = new Thread(new InquireClusterTimer());
        timer.start();
    }

    /**
     * inquire cluster ips
     */
    public static void inquire() throws InterruptedException {
        ClusterInquireMarkModel clusterInquireMarkModel = new ClusterInquireMarkModel();
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.INQUIRE_CLUSTER_NODE_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterInquireMarkModel);
        DefaultPromise<ClusterCenterNodeModel> promise = new DefaultPromise<>(getChannel().eventLoop());
        InquireClusterCenterResponseHandler.promise = promise;
        getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<< send inquire cluster node(ip={}) error >>>>>>>>>>>", CURRENT_USE_CENTER_IP, future.cause());
            }
        });
        if (!promise.await(3, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< inquire cluster ips timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
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
        BOOTSTRAP.remoteAddress(CURRENT_USE_CENTER_IP, DaoCloudConstant.CENTER_PORT);
        BOOTSTRAP.group(group);
        BOOTSTRAP.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new VarIntsProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new IdleStateHandler(8, 0, 0, TimeUnit.SECONDS))
                        .addLast(new InquireClusterCenterResponseHandler())
                        .addLast(new CenterConfigMessageHandler())
                        .addLast(new CenterConfigurationFileMessageHandler())
                        .addLast(new CenterConfigurationPropertyMessageHandler())
                        .addLast(new CenterServerMessageHandler())
                        .addLast(new GatewayPullServiceNodeMessageHandler());
            }
        });
        try {
            CONNECT_CENTER_CHANNEL = BOOTSTRAP.connect().sync().channel();
            log.info(">>>>>>>>> connect center node(ip={}) success. <<<<<<<<<< :)bingo(:", CURRENT_USE_CENTER_IP);
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect center node(ip={}) error >>>>>>>>>>", CURRENT_USE_CENTER_IP, e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public static synchronized void reconnect() {
        if (CONNECT_CENTER_CHANNEL.isActive()) {
            return;
        }
        CONNECT_CENTER_CHANNEL.close().addListener(future -> {
            CONNECT_CENTER_CHANNEL.eventLoop().schedule(() -> {
                BOOTSTRAP.remoteAddress(CURRENT_USE_CENTER_IP, DaoCloudConstant.CENTER_PORT);
                BOOTSTRAP.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (future.isSuccess()) {
                            CONNECT_CENTER_CHANNEL = future.channel();
                            log.info(">>>>>>>>> reconnect center node(ip={}) success. <<<<<<<<<< :)bingo(:", CURRENT_USE_CENTER_IP);
                        } else {
                            log.error("<<<<<<<<<< reconnect center node(ip={}) error. >>>>>>>>>>", CURRENT_USE_CENTER_IP, future.cause());
                            shuffle();
                        }
                    }
                });
            }, 1, TimeUnit.SECONDS);
        });
    }

    /**
     * Filter to connect the cluster ip
     */
    private static void shuffle() {
        if (CLUSTER_CENTER_IP_ITERATOR.hasNext()) {
            CURRENT_USE_CENTER_IP = CLUSTER_CENTER_IP_ITERATOR.next();
            log.info("Switch to the new center node. the address is ip = {}", CURRENT_USE_CENTER_IP);
        }
    }
}
