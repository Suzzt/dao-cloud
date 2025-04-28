package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.CallTrendFullModel;
import com.dao.cloud.core.model.CallTrendPullMarkModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/7/22 16:59
 * Handles getting all call trend data.
 */
@Slf4j
public class CenterClusterCallTrendRequestHandler extends SimpleChannelInboundHandler<CallTrendPullMarkModel> {

    private final RegisterCenterManager registerCenterManager;

    public CenterClusterCallTrendRequestHandler(RegisterCenterManager registerCenterManager) {
        this.registerCenterManager = registerCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CallTrendPullMarkModel msg) throws Exception {
        CallTrendFullModel callTrendFullModel = new CallTrendFullModel();
        try {
            callTrendFullModel.setCallTrendModels(registerCenterManager.getCallTrends());
        } catch (Exception e) {
            callTrendFullModel.setDaoException(new DaoException(CodeEnum.PULL_CALL_TREND_ERROR));
        }
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.INQUIRE_CLUSTER_FULL_CALL_TREND_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, callTrendFullModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send call trend data >>>>>>>>>>>>", future.cause());
            }
        });
    }
}
