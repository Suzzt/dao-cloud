package com.junmo.center.core.handler;

import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.MainProperties;
import com.junmo.core.model.ClusterSyncDataModel;
import com.junmo.core.model.NumberingModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
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
public class SyncClusterInformationRequestHandler extends SimpleChannelInboundHandler<ClusterSyncDataModel> {

    private ConfigCenterManager configCenterManager;

    private ExpireHashMap<Long> expireHashMap;

    public SyncClusterInformationRequestHandler(ConfigCenterManager configCenterManager) {
        expireHashMap = new ExpireHashMap(1000, 1, TimeUnit.HOURS);
        this.configCenterManager = configCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataModel clusterSyncDataModel) {
        NumberingModel numberingModel = new NumberingModel();
        numberingModel.setSequenceId(clusterSyncDataModel.getSequenceId());
        try {
            if (clusterSyncDataModel.getType() == -2) {
                if (!expireHashMap.exists(clusterSyncDataModel.getSequenceId())) {
                    configCenterManager.delete(clusterSyncDataModel.getProxyConfigModel());
                }
            } else if (clusterSyncDataModel.getType() == -1) {
                RegisterProviderModel registerProviderModel = clusterSyncDataModel.getRegisterProviderModel();
                RegisterCenterManager.down(registerProviderModel);
            } else if (clusterSyncDataModel.getType() == 1) {
                RegisterProviderModel registerProviderModel = clusterSyncDataModel.getRegisterProviderModel();
                RegisterCenterManager.register(registerProviderModel);
            } else if (clusterSyncDataModel.getType() == 2) {
                if (!expireHashMap.exists(clusterSyncDataModel.getSequenceId())) {
                    configCenterManager.save(clusterSyncDataModel.getProxyConfigModel(), clusterSyncDataModel.getConfigJson());
                }
            }
            DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, MainProperties.serialize, numberingModel);
            ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("data sync response message", future.cause());
                }
            });
        } catch (Throwable t) {
            numberingModel.setErrorMessage(t.getMessage());
            DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, MainProperties.serialize, numberingModel);
            ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("data sync response message", future.cause());
                }
            });
        }
    }
}
