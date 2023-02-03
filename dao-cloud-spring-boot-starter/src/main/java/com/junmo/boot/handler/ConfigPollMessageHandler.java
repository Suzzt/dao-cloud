package com.junmo.boot.handler;

import com.google.gson.Gson;
import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.core.model.RegisterServerModel;
import com.junmo.core.model.ServerNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/19 09:11
 * @description: poll server node
 */
@Slf4j
public class ConfigPollMessageHandler extends SimpleChannelInboundHandler<RegisterServerModel> {

    public static final Map<String, Promise<List<ServerNodeModel>>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterServerModel registerServerModel) {
        Promise<List<ServerNodeModel>> pollPromise = PROMISE_MAP.remove(registerServerModel.getProxy() + "#" + registerServerModel.getVersion());
        List<ServerNodeModel> serverNodeModes = registerServerModel.getServerNodeModes();
        Exception exceptionValue = registerServerModel.getExceptionValue();
        if (exceptionValue != null) {
            log.error(">>>>>>>>>>>> poll (proxy = {}, version = {}, server node = {}) error. <<<<<<<<<<<<", registerServerModel.getProxy(), registerServerModel.getVersion(), new Gson().toJson(registerServerModel.getServerNodeModes()));
            pollPromise.setFailure(exceptionValue);
        } else {
            log.info(">>>>>>>>>>>> poll (proxy = {}, version = {}, server node = {}) success. <<<<<<<<<<<<", registerServerModel.getProxy(), registerServerModel.getVersion(), new Gson().toJson(registerServerModel.getServerNodeModes()));
            pollPromise.setSuccess(serverNodeModes);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        RegistryManager.reconnect();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            RegistryManager.reconnect();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}