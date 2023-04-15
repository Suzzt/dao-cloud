package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.core.model.CenterModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2023/3/12 22:47
 * @description:
 */
public class ClusterCenterHandler extends SimpleChannelInboundHandler<CenterModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CenterModel centerModel) {
        CenterClusterManager.acceptance(centerModel);
    }
}
