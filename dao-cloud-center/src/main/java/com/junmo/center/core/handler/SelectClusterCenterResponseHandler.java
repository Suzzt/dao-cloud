package com.junmo.center.core.handler;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ClusterCenterNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/3/12 22:47
 * @description:
 */
@Slf4j
public class SelectClusterCenterResponseHandler extends SimpleChannelInboundHandler<ClusterCenterNodeModel> {
    public static Promise<ClusterCenterNodeModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterCenterNodeModel clusterCenterNodeModel) {
        if (promise.isSuccess()) {
            promise.setSuccess(clusterCenterNodeModel);
        } else {
            promise.setFailure(new DaoException("inquire cluster ip error"));
        }
    }
}
