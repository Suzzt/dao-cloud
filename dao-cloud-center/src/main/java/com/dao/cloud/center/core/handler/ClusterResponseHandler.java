package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.CenterClusterManager;
import com.dao.cloud.center.core.cluster.ClusterCenterConnector;
import com.dao.cloud.core.model.HeartbeatModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/7/19 21:49
 * @description:
 */
@Slf4j
public class ClusterResponseHandler extends SimpleChannelInboundHandler<HeartbeatModel> {

    private ClusterCenterConnector connector;

    public ClusterResponseHandler(ClusterCenterConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatModel heartbeatModel) throws Exception {
        connector.clearFailMark();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        CenterClusterManager.down(connector.getConnectIp());
        ctx.channel().close();
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // send heartbeat packet
            connector.sendHeartbeat();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
