package com.junmo.center.core.handler;

import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.model.ClusterSyncDataModel;
import com.junmo.core.model.RegisterProviderModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2023/6/24 00:00
 * @description:
 */
public class SyncClusterInformationHandler extends SimpleChannelInboundHandler<ClusterSyncDataModel> {

    private ConfigCenterManager configCenterManager;

    public SyncClusterInformationHandler(ConfigCenterManager configCenterManager) {
        this.configCenterManager = configCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterSyncDataModel clusterSyncDataModel) {
        if (clusterSyncDataModel.getType() == -2) {
            configCenterManager.delete(clusterSyncDataModel.getProxyConfigModel());
        } else if (clusterSyncDataModel.getType() == -1) {
            RegisterProviderModel registerProviderModel = clusterSyncDataModel.getRegisterProviderModel();
            RegisterCenterManager.down(registerProviderModel);
        } else if (clusterSyncDataModel.getType() == 1) {
            RegisterProviderModel registerProviderModel = clusterSyncDataModel.getRegisterProviderModel();
            RegisterCenterManager.register(registerProviderModel);
        } else if (clusterSyncDataModel.getType() == 2) {
            configCenterManager.save(clusterSyncDataModel.getProxyConfigModel(), clusterSyncDataModel.getConfigJson());
        }
    }
}
