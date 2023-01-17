package com.junmo.config.register.handler;

import com.junmo.config.register.Register;
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
        serverNodeModels = Register.getServers(proxy);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, (byte) 0, new RegisterServerModel(proxy, serverNodeModels));
        ctx.writeAndFlush(daoMessage).addListener(future -> {

        });
    }
}
