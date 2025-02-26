package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.GatewayServiceNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 * Center cluster gateway pull service node handler(All server info)
 */
@Slf4j
public class CenterClusterGatewayConfigResponseMessageHandler extends SimpleChannelInboundHandler<GatewayServiceNodeModel> {

    public static Promise<GatewayServiceNodeModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayServiceNodeModel gatewayServiceNodeModel) {
        DaoException daoException = gatewayServiceNodeModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> The gateway successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(gatewayServiceNodeModel);
        } else {
            log.error("<<<<<<<<<<<< The gateway failed to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(daoException);
        }
    }
}