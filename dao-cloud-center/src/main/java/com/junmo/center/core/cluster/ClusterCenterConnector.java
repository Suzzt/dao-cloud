package com.junmo.center.core.cluster;

import com.junmo.center.core.handler.SelectClusterCenterResponseHandler;
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
    private final int connectPort = 5551;
    private Channel clusterChannel;

    public ClusterCenterConnector(String connectIp) {
        connect(connectIp);
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(new JoinClusterTimer(this));
    }

    public Channel getChannel() {
        return this.clusterChannel;
    }

    public void connect(String ip) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(ip, connectPort);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast(new ProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new SelectClusterCenterResponseHandler());
            }
        });
        try {
            clusterChannel = bootstrap.connect().sync().channel();
            log.info(">>>>>>>>> connect dao-cloud-center cluster success <<<<<<<<<<");
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect dao-cloud-center cluster error >>>>>>>>>>", e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    public void sendHeartbeat() {
        clusterChannel.writeAndFlush(new HeartbeatPacket()).addListeners(future -> {
            if (future.isSuccess()) {
                log.debug(">>>>>>>>> send heart beat cluster success <<<<<<<<<");
            } else {
                log.error("<<<<<<<<< send heart beat cluster error <<<<<<<<<");
                reconnect();
                sendHeartbeat();
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
                            log.info(">>>>>>>>> reconnect center cluster success. <<<<<<<<<< :)bingo(:");
                        } else {
                            log.error("<<<<<<<<<< reconnect center cluster error >>>>>>>>>>", future.cause());
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        });
    }
}