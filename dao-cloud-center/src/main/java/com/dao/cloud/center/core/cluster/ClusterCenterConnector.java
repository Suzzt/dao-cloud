package com.dao.cloud.center.core.cluster;

import com.dao.cloud.center.core.handler.*;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.AbstractShareClusterRequestModel;
import com.dao.cloud.core.netty.protocol.*;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/4/16 23:21
 */
@Slf4j
public class ClusterCenterConnector {
    private final Bootstrap bootstrap = new Bootstrap();
    private String connectIp;
    private volatile Channel clusterChannel;
    /**
     * fail mark count
     */
    private int failMark = 0;
    private volatile boolean state;

    /**
     * 一个标记位: 用于触发集群心跳(因为当节点初始化同步过程中,这时候该节点不能作为服务节点提供能力,因为这时候它没有数据)
     * true: 同步中
     * false: 同步完成,可以作为服务节点提供能力
     */
    private boolean flag;

    /**
     * @param connectIp
     */
    public ClusterCenterConnector(String connectIp, boolean flag) {
        this.connectIp = connectIp;
        this.flag = flag;
        connect();
    }

    public String getConnectIp() {
        return connectIp;
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

    public boolean isActive() {
        return state;
    }

    public boolean isSyncing() {
        return flag;
    }

    public void ready() {
        this.flag = false;
    }

    public void connect() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(connectIp, DaoCloudConstant.CENTER_PORT);
        bootstrap.group(group);
        ClusterResponseHandler clusterRequestHandler = new ClusterResponseHandler(this);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                state = true;
                ch.pipeline()
                        .addLast(new VarIntsProtocolFrameDecoder())
                        .addLast(new DaoMessageCoder())
                        .addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS))
                        .addLast(new InquireClusterCenterResponseHandler())
                        .addLast(new SyncClusterInformationResponseHandler())
                        .addLast(new CenterClusterConfigResponseHandler())
                        .addLast(new CenterClusterGatewayConfigResponseMessageHandler())
                        .addLast(new CenterClusterServerConfigResponseMessageHandler())
                        .addLast(new CenterClusterConfigurationFileResponseMessageHandler())
                        .addLast(new ConfigurationPropertyResponseHandler())
                        .addLast(new CenterClusterCallTrendResponseHandler())
                        .addLast(clusterRequestHandler);
            }
        });
        // 设置连接超时时间
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        try {
            clusterChannel = bootstrap.connect().sync().channel();
            log.info(">>>>>>>>> connect dao-cloud-center cluster (ip={}) success <<<<<<<<<<", connectIp);
        } catch (Exception e) {
            log.error("<<<<<<<<<< connect dao-cloud-center cluster (ip={}) error >>>>>>>>>>", connectIp, e);
            group.shutdownGracefully();
            throw new DaoException(e);
        }
    }

    /**
     * send heartbeats to each other
     */
    public void sendHeartbeat() {
        getChannel().writeAndFlush(new HeartbeatPacket()).addListeners(future -> {
            if (future.isSuccess()) {
                log.info(">>>>>>>>> send heart beat cluster (ip={}) success <<<<<<<<<", connectIp);
            } else {
                log.error("<<<<<<<<< retry = {} send heart beat cluster (ip={}) error <<<<<<<<<", failMark, connectIp);
                state = false;
                reconnect();
            }
        });
        if (failMark > 2) {
            state = false;
        }
        failMark++;
    }

    /**
     * reconnect cluster
     */
    private synchronized void reconnect() {
        if (clusterChannel.isActive()) {
            return;
        }
        clusterChannel.close().addListener(future -> {
            clusterChannel.eventLoop().schedule(() -> {
                bootstrap.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (future.isSuccess()) {
                            clusterChannel = future.channel();
                            failMark = 0;
                            state = true;
                            log.info(">>>>>>>>> reconnect center cluster success. <<<<<<<<<< :)bingo(:");
                        } else {
                            log.error("<<<<<<<<<< reconnect = {} center cluster error >>>>>>>>>>", failMark, future.cause());
                        }
                    }
                });
            }, 1, TimeUnit.SECONDS);
        });
    }

    /**
     * Share and synchronize to all cluster nodes
     *
     * @param requestModel
     */
    public void share(AbstractShareClusterRequestModel requestModel) {
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.SYNC_CLUSTER_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, requestModel);
        getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< send sync data to cluster error >>>>>>>>>", future.cause());
            }
        });
    }

    public void clearFailMark() {
        this.failMark = 0;
        this.state = true;
    }
}