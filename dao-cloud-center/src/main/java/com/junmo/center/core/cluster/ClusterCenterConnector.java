package com.junmo.center.core.cluster;

import com.junmo.center.core.handler.InquireClusterCenterResponseHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.netty.protocol.DaoMessageCoder;
import com.junmo.core.netty.protocol.HeartbeatPacket;
import com.junmo.core.netty.protocol.ProtocolFrameDecoder;
import com.junmo.core.util.ThreadPoolFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/4/16 23:21
 * @description:
 */
@Slf4j
public class ClusterCenterConnector {
    private final Bootstrap bootstrap = new Bootstrap();
    private String connectIp;
    private final int connectPort = 5551;
    private Channel clusterChannel;
    /**
     * fail mark count
     * if >3. it will be eliminated
     */
    private int failMark = 0;

    public ClusterCenterConnector(String connectIp, boolean flag) {
        this.connectIp = connectIp;
        if (flag) {
            ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(new JoinClusterTimer(this));
        }
    }

    /**
     * get center channel
     *
     * @return
     */
    public Channel getChannel() {
        if (clusterChannel == null) {
            connect();
        }
        return clusterChannel;
    }

    public void connect() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(connectIp, connectPort);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new InquireClusterCenterResponseHandler());
            }
        });
        try {
            clusterChannel = bootstrap.connect().sync().channel();
            log.info(">>>>>>>>> connect dao-cloud-center cluster (ip={}) success <<<<<<<<<<", connectIp);
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect dao-cloud-center cluster (ip={}) error >>>>>>>>>>", connectIp, e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public void sendHeartbeat() {
        getChannel().writeAndFlush(new HeartbeatPacket()).addListeners(future -> {
            if (future.isSuccess()) {
                log.info(">>>>>>>>> send heart beat cluster (ip={}) success <<<<<<<<<", connectIp);
            } else {
                log.error("<<<<<<<<< send heart beat cluster (ip={}) error <<<<<<<<<", connectIp);
                if (failMark <= 3) {
                    reconnect();
                    sendHeartbeat();
                }
            }
        });
    }

    private void reconnect() {
        clusterChannel.close().addListener(future -> {
            clusterChannel.eventLoop().schedule(() -> {
                bootstrap.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            clusterChannel = future.channel();
                            failMark = 0;
                            log.info(">>>>>>>>> reconnect center cluster success. <<<<<<<<<< :)bingo(:");
                        } else {
                            failMark++;
                            log.error("<<<<<<<<<< reconnect center cluster error >>>>>>>>>>", future.cause());
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        });
    }
}