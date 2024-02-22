package com.junmo.center.core.handler;

import com.junmo.center.bootstarp.DaoCloudCenterConfiguration;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.*;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;


/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: pull server handler
 */
@Slf4j
public class PullServerHandler extends SimpleChannelInboundHandler<ProxyProviderModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyProviderModel proxyProviderModel) {
        String proxy = proxyProviderModel.getProxy();
        ProviderModel providerModel = proxyProviderModel.getProviderModel();
        Set<ServerNodeModel> serverNodeModels;
        DaoMessage daoMessage;
        try {
            serverNodeModels = RegisterCenterManager.getServers(proxy, providerModel);
            daoMessage = new DaoMessage((byte) 1, MessageType.PULL_REGISTRY_SERVER_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, new ProxyProviderServerModel(proxy, providerModel, serverNodeModels));
        } catch (Exception e) {
            log.error("<<<<<<<<<<< Failed to pull service list >>>>>>>>>>>>", e);
            daoMessage = new DaoMessage((byte) 1, MessageType.PULL_REGISTRY_SERVER_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, new ProxyProviderServerModel(CodeEnum.PULL_SERVICE_NODE_ERROR));
        }
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< send server node info error >>>>>>>>>>>>", future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("<<<<<<<<<< pull server node info error {} >>>>>>>>>", ctx.channel(), cause);
    }
}
