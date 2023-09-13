package com.junmo.center.core.handler;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ClusterSyncDataResponseModel;
import com.junmo.core.util.LongPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.springframework.util.StringUtils;

/**
 * @author: sucf
 * @date: 2023/7/11 18:23
 * @description: 回应cluster数据同步
 */
public class SyncClusterInformationResponseHandler extends SimpleChannelInboundHandler<ClusterSyncDataResponseModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataResponseModel clusterSyncDataResponseModel) throws Exception {
        Promise promise = LongPromiseBuffer.getInstance().remove(clusterSyncDataResponseModel.getSequenceId());
        if (StringUtils.hasLength(clusterSyncDataResponseModel.getErrorMessage())) {
            promise.setFailure(new DaoException(clusterSyncDataResponseModel.getErrorMessage()));
        } else {
            promise.setSuccess(clusterSyncDataResponseModel.getSequenceId());
        }
    }
}
