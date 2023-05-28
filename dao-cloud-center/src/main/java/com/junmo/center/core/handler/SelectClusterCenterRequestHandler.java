package com.junmo.center.core.handler;

import com.junmo.core.model.ClusterInquireMarkModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/3/12 22:47
 * @description:
 */
@Slf4j
public class SelectClusterCenterRequestHandler extends SimpleChannelInboundHandler<ClusterInquireMarkModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterInquireMarkModel clusterInquireMarkModel) {
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.PULL_CLUSTER_RESPONSE_MESSAGE, (byte) 0, clusterInquireMarkModel);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send cluster node error", future.cause());
            }
        });
    }
}
