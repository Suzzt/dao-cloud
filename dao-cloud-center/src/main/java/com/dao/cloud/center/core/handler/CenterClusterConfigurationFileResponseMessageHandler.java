package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigurationFileResponseModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 */
@Slf4j
public class CenterClusterConfigurationFileResponseMessageHandler extends SimpleChannelInboundHandler<ConfigurationFileResponseModel> {

    public static Promise<ConfigurationFileResponseModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationFileResponseModel configurationFileResponseModel) {
        DaoException daoException = configurationFileResponseModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> The configuration file successfully pulled all nodes. <<<<<<<<<<<<");
            promise.setSuccess(configurationFileResponseModel);
        } else {
            log.error("<<<<<<<<<<<< The configuration file to pull all nodes. >>>>>>>>>>>>");
            promise.setFailure(daoException);
        }
    }
}