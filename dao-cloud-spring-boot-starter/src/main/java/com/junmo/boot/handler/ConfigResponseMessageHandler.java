package com.junmo.boot.handler;

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
 * @description:
 */
@Slf4j
public class ConfigResponseMessageHandler extends SimpleChannelInboundHandler<RegisterServerModel> {

    public static final Map<String, Promise<List<ServerNodeModel>>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterServerModel registerServerModel) {
        // get promise
        Promise<List<ServerNodeModel>> promise = PROMISE_MAP.get(registerServerModel.getProxy());
        if (promise != null) {
            List<ServerNodeModel> serverNodeModes = registerServerModel.getServerNodeModes();
            Exception exceptionValue = registerServerModel.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(serverNodeModes);
            }
        }
    }
}