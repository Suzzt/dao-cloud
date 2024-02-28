package com.junmo.center.core.handler;

import com.google.gson.Gson;
import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.GatewayCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.*;
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
public class SyncClusterInformationRequestHandler extends SimpleChannelInboundHandler<AbstractShareClusterRequestModel> {

    private final int DELETE_GATEWAY = -3;

    private final int DELETE_CONFIG = -2;

    private final int DOWN_SERVER = -1;

    private final int UP_SERVER = 1;

    private final int SAVE_CONFIG = 2;

    private final int SAVE_GATEWAY = 3;

    private ConfigCenterManager configCenterManager;

    private GatewayCenterManager gatewayCenterManager;

    private ExpireHashMap<Long> expireHashMap;

    public SyncClusterInformationRequestHandler(ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        expireHashMap = new ExpireHashMap(1000, 1, TimeUnit.HOURS);
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractShareClusterRequestModel shareClusterRequestModel) {
        try {
            if (shareClusterRequestModel.getType() == DELETE_GATEWAY) {
                if (!expireHashMap.exists(shareClusterRequestModel.getSequenceId())) {
                    GatewayShareClusterRequestModel gatewayShareClusterRequestModel = (GatewayShareClusterRequestModel) shareClusterRequestModel;
                    gatewayCenterManager.clear(gatewayShareClusterRequestModel.getProxyProviderModel());
                }
                answer(ctx, shareClusterRequestModel);
            } else if (shareClusterRequestModel.getType() == DELETE_CONFIG) {
                if (!expireHashMap.exists(shareClusterRequestModel.getSequenceId())) {
                    ConfigShareClusterRequestModel configShareClusterRequestModel = (ConfigShareClusterRequestModel) shareClusterRequestModel;
                    configCenterManager.delete(configShareClusterRequestModel.getProxyConfigModel());
                }
                answer(ctx, shareClusterRequestModel);
            } else if (shareClusterRequestModel.getType() == DOWN_SERVER) {
                ServiceShareClusterRequestModel serviceShareClusterRequestModel = (ServiceShareClusterRequestModel) shareClusterRequestModel;
                RegisterProviderModel registerProviderModel = serviceShareClusterRequestModel.getRegisterProviderModel();
                RegisterCenterManager.down(registerProviderModel);
            } else if (shareClusterRequestModel.getType() == UP_SERVER) {
                ServiceShareClusterRequestModel serviceShareClusterRequestModel = (ServiceShareClusterRequestModel) shareClusterRequestModel;
                RegisterProviderModel registerProviderModel = serviceShareClusterRequestModel.getRegisterProviderModel();
                RegisterCenterManager.register(registerProviderModel);
            } else if (shareClusterRequestModel.getType() == SAVE_CONFIG) {
                if (!expireHashMap.exists(shareClusterRequestModel.getSequenceId())) {
                    ConfigShareClusterRequestModel configShareClusterRequestModel = (ConfigShareClusterRequestModel) shareClusterRequestModel;
                    configCenterManager.save(configShareClusterRequestModel.getProxyConfigModel(), configShareClusterRequestModel.getConfigJson());
                }
                answer(ctx, shareClusterRequestModel);
            } else if (shareClusterRequestModel.getType() == SAVE_GATEWAY) {
                if (!expireHashMap.exists(shareClusterRequestModel.getSequenceId())) {
                    GatewayShareClusterRequestModel gatewayShareClusterRequestModel = (GatewayShareClusterRequestModel) shareClusterRequestModel;
                    gatewayCenterManager.save(gatewayShareClusterRequestModel.getProxyProviderModel(), gatewayShareClusterRequestModel.getGatewayConfigModel());
                }
                answer(ctx, shareClusterRequestModel);
            } else {
                log.error("The type of synchronized data={} dao-cloud cannot handle it", new Gson().toJson(shareClusterRequestModel));
                ClusterSyncDataResponseModel clusterSyncDataResponseModel = new ClusterSyncDataResponseModel();
                clusterSyncDataResponseModel.setDaoException(new DaoException(CodeEnum.SYNC_DATA_EXTRA_TYPE_ERROR));
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterSyncDataResponseModel);
                ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("data sync response message", future.cause());
                    }
                });
            }
        } catch (Throwable t) {
            log.error("Sharing data={} between clusters accept handle error", t, new Gson().toJson(shareClusterRequestModel));
            if (shareClusterRequestModel.getType() == DOWN_SERVER || shareClusterRequestModel.getType() == SAVE_CONFIG) {
                ClusterSyncDataResponseModel clusterSyncDataResponseModel = new ClusterSyncDataResponseModel();
                clusterSyncDataResponseModel.setDaoException(new DaoException(CodeEnum.SYNC_SHARE_CLUSTER_DATA_ERROR));
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterSyncDataResponseModel);
                ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("data sync response message", future.cause());
                    }
                });
            }
        }
    }

    /**
     * Reply to request
     *
     * @param ctx
     * @param shareClusterRequestModel
     */
    private void answer(ChannelHandlerContext ctx, AbstractShareClusterRequestModel shareClusterRequestModel) {
        ClusterSyncDataResponseModel clusterSyncDataResponseModel = new ClusterSyncDataResponseModel();
        clusterSyncDataResponseModel.setSequenceId(shareClusterRequestModel.getSequenceId());
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, clusterSyncDataResponseModel);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("data sync response message", future.cause());
            }
        });
    }
}
