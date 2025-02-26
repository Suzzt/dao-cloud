package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.CallTrendFullModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 */
@Slf4j
public class CenterClusterCallTrendResponseHandler extends SimpleChannelInboundHandler<CallTrendFullModel> {

    public static Promise<CallTrendFullModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CallTrendFullModel callTrendFullModel) {
        DaoException daoException = callTrendFullModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> The call trend successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(callTrendFullModel);
        } else {
            log.error("<<<<<<<<<<<< The call trend failed to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(daoException);
        }
    }
}
