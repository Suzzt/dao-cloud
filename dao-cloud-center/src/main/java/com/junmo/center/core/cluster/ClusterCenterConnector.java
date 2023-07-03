package com.junmo.center.core.cluster;

import com.junmo.center.core.handler.InquireClusterCenterResponseHandler;
import com.junmo.core.MainProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ClusterSyncDataModel;
import com.junmo.core.netty.protocol.*;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.DaoTimer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
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
    private Channel clusterChannel;
    /**
     * fail mark count
     * if >3. it will be eliminated
     */
    private int failMark = 0;
    private ClusterTimerTask clusterTimerTask;

    public ClusterCenterConnector(String connectIp, boolean flag) {
        this.connectIp = connectIp;
        if (flag) {
            this.clusterTimerTask = new ClusterTimerTask();
            sendHeartbeat();
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(clusterTimerTask, 5, TimeUnit.SECONDS);
        }
    }

    private class ClusterTimerTask implements TimerTask {
        private boolean remove;

        @Override
        public void run(Timeout timeout) {
            if (remove) {
                return;
            }
            try {
                sendHeartbeat();
            } catch (Exception e) {
                log.error("<<<<<<<<< join cluster error >>>>>>>>>", e);
            } finally {
                DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
            }
        }
    }

    public void cancel() {
        clusterTimerTask.remove = true;
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
        bootstrap.remoteAddress(connectIp, DaoCloudConstant.CENTER_IP);
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
                } else {
                    cancel();
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

    public void syncData(ClusterSyncDataModel clusterSyncDataModel) {
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.SYNC_CLUSTER_SERVER_MESSAGE, MainProperties.serialize, clusterSyncDataModel);
        getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< send register server error >>>>>>>>>", future.cause());
            }
        });
    }
}