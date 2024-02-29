package com.dao.cloud.starter.handler;

import com.dao.cloud.starter.bootstrap.manager.CenterChannelManager;
import com.google.gson.Gson;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ProxyProviderServerModel;
import com.dao.cloud.core.model.ServerNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/19 09:11
 * @description: pull server node
 */
@Slf4j
public class CenterServerMessageHandler extends SimpleChannelInboundHandler<ProxyProviderServerModel> {

    public static final Map<ProxyProviderModel, Promise<Set<ServerNodeModel>>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyProviderServerModel proxyProviderServerModel) {
        String proxy = proxyProviderServerModel.getProxy();
        ProviderModel providerModel = proxyProviderServerModel.getProviderModel();
        Promise<Set<ServerNodeModel>> pullPromise = PROMISE_MAP.remove(new ProxyProviderModel(proxy, providerModel));
        Set<ServerNodeModel> serverNodeModes = proxyProviderServerModel.getServerNodeModes();
        DaoException daoException = proxyProviderServerModel.getDaoException();
        if (daoException == null) {
            log.info(">>>>>>>>>>>> pull (proxy = {}, provider = {}, server node = {}) success. <<<<<<<<<<<<", proxyProviderServerModel.getProxy(), proxyProviderServerModel.getProviderModel(), new Gson().toJson(proxyProviderServerModel.getServerNodeModes()));
            pullPromise.setSuccess(serverNodeModes);
        } else {
            log.error("<<<<<<<<<<<< pull (proxy = {}, provider = {}, server node = {}) error. >>>>>>>>>>>>", proxyProviderServerModel.getProxy(), proxyProviderServerModel.getProviderModel(), new Gson().toJson(proxyProviderServerModel.getServerNodeModes()));
            pullPromise.setFailure(daoException);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        CenterChannelManager.reconnect();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            CenterChannelManager.reconnect();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}