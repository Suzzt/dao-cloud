package com.dao.cloud.starter.handler;

import com.dao.cloud.core.model.ClusterCenterNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 */
@Slf4j
public class InquireClusterCenterResponseHandler extends SimpleChannelInboundHandler<ClusterCenterNodeModel> {
    public static Promise<ClusterCenterNodeModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterCenterNodeModel clusterCenterNodeModel) {
        promise.setSuccess(clusterCenterNodeModel);
    }
}
