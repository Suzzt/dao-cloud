package com.junmo.boot.handler;

import com.google.gson.Gson;
import com.junmo.core.model.RegisterServerModel;
import com.junmo.core.model.ServerNodeModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/19 09:11
 * @description: heart beat to center || server register || poll server node
 */
@Slf4j
public class ConfigResponseMessageHandler extends SimpleChannelInboundHandler<RegisterServerModel> {

    public static final Map<String, Promise<List<ServerNodeModel>>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterServerModel registerServerModel) {
        Promise<List<ServerNodeModel>> pollPromise = PROMISE_MAP.remove(registerServerModel.getProxy());
        if (pollPromise != null) {
            List<ServerNodeModel> serverNodeModes = registerServerModel.getServerNodeModes();
            Exception exceptionValue = registerServerModel.getExceptionValue();
            if (exceptionValue != null) {
                pollPromise.setFailure(exceptionValue);
            } else {
                pollPromise.setSuccess(serverNodeModes);
            }
            log.info(">>>>>>>>>>>> poll (proxy = {}, server node = {}) success. <<<<<<<<<<<<", registerServerModel.getProxy(), new Gson().toJson(registerServerModel.getServerNodeModes()));
        } else {
            log.info(">>>>>>>>>>>> send heart beat to center || server register success <<<<<<<<<<<<");
        }
    }
}