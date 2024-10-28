package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.ConfigCenterManager;
import com.dao.cloud.center.core.GatewayCenterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description: 请求处理cluster数据同步
 */
@Slf4j
public class SyncClusterInformationRequestHandler extends SimpleChannelInboundHandler<AbstractShareClusterRequestModel> {

    public static final byte CALL_TREND_CLEAR = -5;
    public static final byte DELETE_GATEWAY = -3;
    public static final byte DELETE_CONFIG = -2;
    public static final byte DOWN_SERVER = -1;
    public static final byte UP_SERVER = 1;
    public static final byte SAVE_CONFIG = 2;
    public static final byte SAVE_GATEWAY = 3;
    public static final byte SERVER_STATUS = 4;
    public static final byte CALL_TREND_INCREMENT = 5;

    private final Cache<Long, Boolean> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final ConfigCenterManager configCenterManager;
    private final GatewayCenterManager gatewayCenterManager;
    private final RegisterCenterManager registerCenterManager;

    public SyncClusterInformationRequestHandler(RegisterCenterManager registerCenterManager, ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        this.registerCenterManager = registerCenterManager;
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractShareClusterRequestModel shareClusterRequestModel) {
        try {
            boolean processed = handleRequest(shareClusterRequestModel);
            if (processed) {
                answer(ctx, shareClusterRequestModel);
            } else {
                sendErrorResponse(ctx, shareClusterRequestModel, CodeEnum.SYNC_DATA_EXTRA_TYPE_ERROR);
            }
        } catch (Exception e) {
            log.error("Cluster data sync error: {}", new Gson().toJson(shareClusterRequestModel), e);
            sendErrorResponse(ctx, shareClusterRequestModel, CodeEnum.SYNC_SHARE_CLUSTER_DATA_ERROR);
        }
    }

    /**
     * Handles the request based on its type.
     *
     * @param shareClusterRequestModel the model of the request
     * @return true if the request was handled successfully, false if the type is unsupported
     */
    private boolean handleRequest(AbstractShareClusterRequestModel shareClusterRequestModel) {
        byte type = shareClusterRequestModel.getType();

        if (isProcessed(shareClusterRequestModel)) {
            return true;
        }

        switch (type) {
            case DELETE_GATEWAY:
                gatewayCenterManager.clear(((GatewayShareClusterRequestModel) shareClusterRequestModel).getProxyProviderModel());
                break;
            case DELETE_CONFIG:
                configCenterManager.delete(((ConfigShareClusterRequestModel) shareClusterRequestModel).getProxyConfigModel());
                break;
            case DOWN_SERVER:
                registerCenterManager.unregistered(((ServiceShareClusterRequestModel) shareClusterRequestModel).getRegisterProviderModel());
                break;
            case UP_SERVER:
                registerCenterManager.registry(((ServiceShareClusterRequestModel) shareClusterRequestModel).getRegisterProviderModel());
                break;
            case SAVE_CONFIG:
                ConfigShareClusterRequestModel configRequest = (ConfigShareClusterRequestModel) shareClusterRequestModel;
                configCenterManager.save(configRequest.getProxyConfigModel(), configRequest.getConfigJson());
                break;
            case SAVE_GATEWAY:
                GatewayShareClusterRequestModel gatewayRequest = (GatewayShareClusterRequestModel) shareClusterRequestModel;
                gatewayCenterManager.save(gatewayRequest.getProxyProviderModel(), gatewayRequest.getGatewayConfigModel());
                break;
            case SERVER_STATUS:
                ServerShareClusterRequestModel serverRequest = (ServerShareClusterRequestModel) shareClusterRequestModel;
                registerCenterManager.manage(serverRequest.getProxyProviderModel(), serverRequest.getServerNodeModel());
                break;
            case CALL_TREND_INCREMENT:
                registerCenterManager.callTrendIncrement(((CallTrendShareClusterRequestModel) shareClusterRequestModel).getCallTrendModel());
                break;
            case CALL_TREND_CLEAR:
                CallTrendShareClusterRequestModel callTrendRequest = (CallTrendShareClusterRequestModel) shareClusterRequestModel;
                registerCenterManager.callTrendClear(callTrendRequest.getCallTrendModel().getProxyProviderModel(), callTrendRequest.getCallTrendModel().getMethodName());
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Checks if a request has already been processed based on its sequence ID.
     *
     * @param shareClusterRequestModel the model of the request
     * @return true if the request has already been processed, otherwise false
     */
    private boolean isProcessed(AbstractShareClusterRequestModel shareClusterRequestModel) {
        long sequenceId = shareClusterRequestModel.getSequenceId();
        if (cache.getIfPresent(sequenceId) != null) {
            return true;
        }
        cache.put(sequenceId, true);
        return false;
    }

    /**
     * Sends an error response back to the client.
     *
     * @param ctx the channel context
     * @param shareClusterRequestModel the model of the request
     * @param codeEnum the error code to send
     */
    private void sendErrorResponse(ChannelHandlerContext ctx, AbstractShareClusterRequestModel shareClusterRequestModel, CodeEnum codeEnum) {
        ClusterSyncDataResponseModel response = new ClusterSyncDataResponseModel();
        response.setDaoException(new DaoException(codeEnum));
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, response);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("Failed to send error response", future.cause());
            }
        });
    }

    /**
     * Sends a success response with the sequence ID.
     *
     * @param ctx the channel context
     * @param shareClusterRequestModel the model of the request
     */
    private void answer(ChannelHandlerContext ctx, AbstractShareClusterRequestModel shareClusterRequestModel) {
        ClusterSyncDataResponseModel response = new ClusterSyncDataResponseModel();
        response.setSequenceId(shareClusterRequestModel.getSequenceId());
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.SYNC_CLUSTER_SERVER_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, response);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("Failed to send response message", future.cause());
            }
        });
    }
}