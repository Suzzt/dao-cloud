package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.GatewayCenterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.GatewayConfigPullMarkModel;
import com.dao.cloud.core.model.GatewayServiceNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/1/12 22:53
 * @description: Gateway pull service node handler(All server info)
 */
@Slf4j
public class GatewayServiceConfigHandler extends SimpleChannelInboundHandler<GatewayConfigPullMarkModel> {

    private GatewayCenterManager gatewayCenterManager;

    private RegisterCenterManager registerCenterManager;

    public GatewayServiceConfigHandler(RegisterCenterManager registerCenterManager, GatewayCenterManager gatewayCenterManager) {
        this.registerCenterManager = registerCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayConfigPullMarkModel gatewayConfigPullMarkModel) throws Exception {
        DaoMessage daoMessage;
        GatewayServiceNodeModel gatewayServiceNodeModel = new GatewayServiceNodeModel();
        try {
            gatewayServiceNodeModel.setServices(registerCenterManager.gatewayServers());
            gatewayServiceNodeModel.setConfig(gatewayCenterManager.getGatewayConfig());
        } catch (Exception e) {
            gatewayServiceNodeModel.setDaoException(new DaoException(CodeEnum.PULL_GATEWAY_CONFIG_ERROR));
        }
        daoMessage = new DaoMessage((byte) 1, MessageType.GATEWAY_REGISTER_ALL_SERVER_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, gatewayServiceNodeModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send service instance to gateway >>>>>>>>>>>>", future.cause());
            }
        });
    }
}
