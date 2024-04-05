package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ProxyProviderServerModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
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
public class PullServerRequestHandler extends SimpleChannelInboundHandler<ProxyProviderModel> {

    private RegisterCenterManager registerCenterManager;

    public PullServerRequestHandler(RegisterCenterManager registerCenterManager) {
        this.registerCenterManager = registerCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyProviderModel proxyProviderModel) {
        String proxy = proxyProviderModel.getProxy();
        ProviderModel providerModel = proxyProviderModel.getProviderModel();
        Set<ServerNodeModel> serverNodeModels;
        DaoMessage daoMessage;
        try {
            serverNodeModels = registerCenterManager.getServers(proxy, providerModel);
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
}
