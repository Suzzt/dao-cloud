package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.core.model.ClusterCenterNodeModel;
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
public class InquireClusterCenterRequestHandler extends SimpleChannelInboundHandler<ClusterInquireMarkModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterInquireMarkModel clusterInquireMarkModel) {
        ClusterCenterNodeModel clusterCenterNodeModel = new ClusterCenterNodeModel();
        clusterCenterNodeModel.setClusterNodes(CenterClusterManager.aliveNode());
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_NODE_RESPONSE_MESSAGE, (byte) 0, clusterCenterNodeModel);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send cluster node error", future.cause());
            }
        });
    }
}
