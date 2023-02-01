package com.junmo.config.register.handler;

import com.junmo.config.register.Register;
import com.junmo.config.register.RegisterConfig;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterPollModel;
import com.junmo.core.model.RegisterServerModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: poll server handler
 */
@Slf4j
public class PollServerHandler extends SimpleChannelInboundHandler<RegisterPollModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterPollModel model) {
        String proxy = model.getProxy();
        List<ServerNodeModel> serverNodeModels;
        DaoMessage daoMessage;
        try {
            serverNodeModels = Register.getServers(proxy);
            daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, RegisterConfig.SERIALIZE_TYPE, new RegisterServerModel(proxy, serverNodeModels));
        } catch (Exception e) {
            daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, RegisterConfig.SERIALIZE_TYPE, new RegisterServerModel(new DaoException(e)));
        }
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< send server node info error >>>>>>>>>>>>", future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("<<<<<<<<<< poll server node info error >>>>>>>>>", cause);
    }
}
