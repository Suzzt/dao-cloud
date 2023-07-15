package com.junmo.center.core.handler;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ClusterSyncDataResponseModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2023/7/11 18:23
 * @description: 回应cluster数据同步
 */
public class SyncClusterInformationResponseHandler extends SimpleChannelInboundHandler<ClusterSyncDataResponseModel> {

    public static final Map<Long, Promise> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataResponseModel clusterSyncDataResponseModel) throws Exception {
        Promise promise = PROMISE_MAP.remove(clusterSyncDataResponseModel.getSequenceId());
        if (StringUtils.hasLength(clusterSyncDataResponseModel.getErrorMessage())) {
            promise.setFailure(new DaoException(clusterSyncDataResponseModel.getErrorMessage()));
        } else {
            promise.setSuccess(clusterSyncDataResponseModel.getSequenceId());
        }
    }
}
