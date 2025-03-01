package com.dao.cloud.starter.handler;

import com.dao.cloud.core.model.ConfigurationFileInformationResponseModel;
import com.dao.cloud.core.util.LongPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * @author sucf
 * @since 1.0
 */
public class CenterConfigurationFileMessageHandler extends SimpleChannelInboundHandler<ConfigurationFileInformationResponseModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationFileInformationResponseModel msg) throws Exception {
        Promise<Object> promise = LongPromiseBuffer.getInstance().remove(msg.getSequenceId());
        if (msg.getDaoException() == null) {
            promise.setSuccess(msg.getFileNames());
        } else {
            promise.setFailure(msg.getDaoException());
        }
    }
}
