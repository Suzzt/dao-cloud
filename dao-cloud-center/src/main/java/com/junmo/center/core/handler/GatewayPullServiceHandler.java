package com.junmo.center.core.handler;

import com.junmo.center.bootstarp.DaoCloudCenterConfiguration;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.model.GatewayPullServiceMarkModel;
import com.junmo.core.model.GatewayServiceNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/1/12 22:53
 * @description:
 */
@Slf4j
public class GatewayPullServiceHandler extends SimpleChannelInboundHandler<GatewayPullServiceMarkModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayPullServiceMarkModel gatewayPullServiceMarkModel) throws Exception {
        DaoMessage daoMessage;
        GatewayServiceNodeModel gatewayServiceNodeModel = new GatewayServiceNodeModel();
        try {
            gatewayServiceNodeModel.setRegistryServiceNodes(RegisterCenterManager.gatewayServers());
        } catch (Exception e) {
            gatewayServiceNodeModel.setErrorMessage(e.getMessage());
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
