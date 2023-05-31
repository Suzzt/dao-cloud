package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.core.model.ClusterSyncServerModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2023/6/1 00:00
 * @description:
 */
public class SyncServerInformationHandler extends SimpleChannelInboundHandler<ClusterSyncServerModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncServerModel clusterSyncServerModel) {
        CenterClusterManager.SyncServerHandler.accept(clusterSyncServerModel);
    }
}
