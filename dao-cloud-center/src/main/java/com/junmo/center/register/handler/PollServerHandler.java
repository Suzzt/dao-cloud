package com.junmo.center.register.handler;

import com.junmo.center.register.RegisterCenterConfiguration;
import com.junmo.center.register.RegisterClient;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterProxyModel;
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
public class PollServerHandler extends SimpleChannelInboundHandler<RegisterProxyModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProxyModel registerProxyModel) {
        String proxy = registerProxyModel.getProxy();
        int version = registerProxyModel.getVersion();
        List<ServerNodeModel> serverNodeModels;
        DaoMessage daoMessage;
        try {
            serverNodeModels = RegisterClient.getServers(proxy);
            daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, RegisterCenterConfiguration.SERIALIZE_TYPE, new RegisterServerModel(proxy, version, serverNodeModels));
        } catch (Exception e) {
            daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.POLL_REGISTRY_SERVER_RESPONSE_MESSAGE, RegisterCenterConfiguration.SERIALIZE_TYPE, new RegisterServerModel(new DaoException(e)));
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
