package com.junmo.center.core.handler;

import com.google.gson.Gson;
import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.model.ClusterSyncDataRequestModel;
import com.junmo.core.model.ClusterSyncDataResponseModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.ExpireHashMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/6/24 00:00
 * @description: 请求处理cluster数据同步
 */
@Slf4j
public class SyncClusterInformationRequestHandler extends SimpleChannelInboundHandler<ClusterSyncDataRequestModel> {

    private ConfigCenterManager configCenterManager;

    private ExpireHashMap<Long> expireHashMap;

    public SyncClusterInformationRequestHandler(ConfigCenterManager configCenterManager) {
        expireHashMap = new ExpireHashMap(1000, 1, TimeUnit.HOURS);
        this.configCenterManager = configCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataRequestModel clusterSyncDataRequestModel) {
        try {
            if (clusterSyncDataRequestModel.getType() == -2) {
                if (!expireHashMap.exists(clusterSyncDataRequestModel.getSequenceId())) {
                    configCenterManager.delete(clusterSyncDataRequestModel.getProxyConfigModel());
                }
                configAnswer(ctx, clusterSyncDataRequestModel);
            } else if (clusterSyncDataRequestModel.getType() == -1) {
                RegisterProviderModel registerProviderModel = clusterSyncDataRequestModel.getRegisterProviderModel();
                RegisterCenterManager.down(registerProviderModel);
            } else if (clusterSyncDataRequestModel.getType() == 1) {
                RegisterProviderModel registerProviderModel = clusterSyncDataRequestModel.getRegisterProviderModel();
                RegisterCenterManager.register(registerProviderModel);
            } else if (clusterSyncDataRequestModel.getType() == 2) {
                if (!expireHashMap.exists(clusterSyncDataRequestModel.getSequenceId())) {
                    configCenterManager.save(clusterSyncDataRequestModel.getProxyConfigModel(), clusterSyncDataRequestModel.getConfigJson());
                }
                configAnswer(ctx, clusterSyncDataRequestModel);
            }
        } catch (Throwable t) {
            log.error("cluster sync data={} accept handle error", t, new Gson().toJson(clusterSyncDataRequestModel));
            if (clusterSyncDataRequestModel.getType() == -2 || clusterSyncDataRequestModel.getType() == 2) {
                ClusterSyncDataResponseModel clusterSyncDataResponseModel = new ClusterSyncDataResponseModel();
                clusterSyncDataResponseModel.setErrorMessage(t.getMessage());
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterSyncDataResponseModel);
                ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("data sync response message", future.cause());
                    }
                });
            }
        }
    }

    private void configAnswer(ChannelHandlerContext ctx, ClusterSyncDataRequestModel clusterSyncDataRequestModel) {
        ClusterSyncDataResponseModel clusterSyncDataResponseModel = new ClusterSyncDataResponseModel();
        clusterSyncDataResponseModel.setSequenceId(clusterSyncDataRequestModel.getSequenceId());
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterSyncDataResponseModel);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("data sync response message", future.cause());
            }
        });
    }
}
