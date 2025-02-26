package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.model.ClusterSyncDataResponseModel;
import com.dao.cloud.core.util.LongPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * @author sucf
 * @since 1.0
 * 回应cluster数据同步
 */
public class SyncClusterInformationResponseHandler extends SimpleChannelInboundHandler<ClusterSyncDataResponseModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataResponseModel clusterSyncDataResponseModel) throws Exception {
        Promise promise = LongPromiseBuffer.getInstance().remove(clusterSyncDataResponseModel.getSequenceId());
        if (clusterSyncDataResponseModel.getDaoException() == null) {
            promise.setSuccess(clusterSyncDataResponseModel.getSequenceId());
        } else {
            promise.setFailure(clusterSyncDataResponseModel.getDaoException());
        }
    }
}
