package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ErrorResponseModel;
import com.dao.cloud.core.model.Model;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/4/25 22:54
 * @description: Node available handler
 */
@Slf4j
public class AvailableHandler extends SimpleChannelInboundHandler<Model> {

    private boolean available;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Model msg) throws Exception {
        if (!available) {
            ErrorResponseModel responseModel = new ErrorResponseModel();
            responseModel.setDaoException(new DaoException("The current node is not ready"));
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.ERROR_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, responseModel);
            ctx.writeAndFlush(daoMessage).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<<< Failed to send service configuration data >>>>>>>>>>>>", future.cause());
                }
            });
        }
        ctx.fireChannelRead(msg);
    }

    public void ready() {
        this.available = true;
    }
}
