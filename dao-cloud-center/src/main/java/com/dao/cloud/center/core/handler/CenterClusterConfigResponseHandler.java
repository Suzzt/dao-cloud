package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.FullConfigModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 * Center cluster config pull service node handler
 */
@Slf4j
public class CenterClusterConfigResponseHandler extends SimpleChannelInboundHandler<FullConfigModel> {

    public static Promise<FullConfigModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullConfigModel fullConfigModel) {
        DaoException daoException = fullConfigModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> The config successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(fullConfigModel);
        } else {
            log.error("<<<<<<<<<<<< The config failed to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(daoException);
        }
    }
}
