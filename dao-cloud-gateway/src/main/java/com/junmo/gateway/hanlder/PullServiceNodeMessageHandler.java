package com.junmo.gateway.hanlder;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.GatewayServiceNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author: sucf
 * @date: 2024/1/11 23:37
 * @description: Gateway pull service node handler
 */
@Slf4j
public class PullServiceNodeMessageHandler extends SimpleChannelInboundHandler<GatewayServiceNodeModel> {

    public static Promise<GatewayServiceNodeModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayServiceNodeModel gatewayServiceNodeModel) throws Exception {
        String errorMessage = gatewayServiceNodeModel.getErrorMessage();
        if (StringUtils.hasLength(errorMessage)) {
            log.error("<<<<<<<<<<<< The gateway failed to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(new DaoException(errorMessage));
        } else {
            log.info(">>>>>>>>>>>> The gateway successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(gatewayServiceNodeModel);
        }
    }
}
