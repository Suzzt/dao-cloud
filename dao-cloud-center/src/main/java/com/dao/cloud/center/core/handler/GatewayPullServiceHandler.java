package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.GatewayCenterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.GatewayPullServiceMarkModel;
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
public class GatewayPullServiceHandler extends SimpleChannelInboundHandler<GatewayPullServiceMarkModel> {

    private GatewayCenterManager gatewayCenterManager;

    public GatewayPullServiceHandler(GatewayCenterManager gatewayCenterManager) {
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayPullServiceMarkModel gatewayPullServiceMarkModel) throws Exception {
        DaoMessage daoMessage;
        GatewayServiceNodeModel gatewayServiceNodeModel = new GatewayServiceNodeModel();
        try {
            gatewayServiceNodeModel.setServices(RegisterCenterManager.gatewayServers());
            gatewayServiceNodeModel.setConfig(gatewayCenterManager.getGatewayConfig());
        } catch (Exception e) {
            gatewayServiceNodeModel.setDaoException(new DaoException(CodeEnum.PULL_SERVICE_NODE_ERROR));
        }
        daoMessage = new DaoMessage((byte) 1, MessageType.GATEWAY_REGISTER_ALL_SERVER_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, gatewayServiceNodeModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send service instance to gateway >>>>>>>>>>>>", future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("<<<<<<<<<< gateway pull all service node error {} >>>>>>>>>", ctx.channel(), cause);
    }
}
