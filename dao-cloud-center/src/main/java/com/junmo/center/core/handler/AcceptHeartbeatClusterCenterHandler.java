package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.center.core.cluster.ClusterCenterConnector;
import com.junmo.core.model.HeartbeatModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2023/4/16 23:03
 * @description:
 */
public class AcceptHeartbeatClusterCenterHandler extends SimpleChannelInboundHandler<HeartbeatModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatModel msg) {
        // get cluster ip
        String ip = ctx.channel().remoteAddress().toString();
        CenterClusterManager.clusterMap.put(ip, new ClusterCenterConnector(ip));
    }
}
