package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.boot.bootstrap.unit.Client;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.HeartbeatPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcClientMessageHandler extends SimpleChannelInboundHandler<RpcResponseModel> {

    public static final Map<Long, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    private ProxyProviderModel proxyProviderModel;

    private Client client;

    public RpcClientMessageHandler(ProxyProviderModel proxyProviderModel, Client client) {
        this.proxyProviderModel = proxyProviderModel;
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseModel msg) throws Exception {
        // get promise
        Promise<Object> promise = PROMISE_MAP.remove(msg.getSequenceId());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            String errorMessage = msg.getErrorMessage();
            if (StringUtils.hasLength(errorMessage)) {
                promise.setFailure(new DaoException(errorMessage));
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        client.destroy();
        ClientManager.remove(proxyProviderModel, client);
        log.error(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            int failMark = client.getFailMark();
            if (failMark >= 3) {
                client.destroy();
                ClientManager.remove(proxyProviderModel, client);
                log.error("<<<<<<<<<<< server (connect address = {}) down >>>>>>>>>>>", ctx.channel().remoteAddress());
            } else{
                // send heartbeat packet
                sendHeartBeat(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void sendHeartBeat(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        HeartbeatPacket heartbeatPacket = new HeartbeatPacket();
        channel.writeAndFlush(heartbeatPacket).addListener(future -> {
            if (!future.isSuccess()) {
                client.reconnect();
            }
            client.addFailMark();
        });
    }
}