package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ServerConfigModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 * Center cluster server config pull service node handler(All server info)
 */
@Slf4j
public class CenterClusterServerConfigResponseMessageHandler extends SimpleChannelInboundHandler<ServerConfigModel> {

    public static Promise<ServerConfigModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerConfigModel serverConfigModel) {
        DaoException daoException = serverConfigModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> The server config successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(serverConfigModel);
        } else {
            log.error("<<<<<<<<<<<< The server config to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(daoException);
        }
    }
}