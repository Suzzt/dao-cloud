package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.HeartbeatPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

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

    private String proxy;

    private int version;

    private ChannelClient channelClient;

    public RpcClientMessageHandler(String proxy, int version, ChannelClient channelClient) {
        this.proxy = proxy;
        this.version = version;
        this.channelClient = channelClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseModel msg) throws Exception {
        // get promise
        Promise<Object> promise = PROMISE_MAP.remove(msg.getSequenceId());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            DaoException exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelClient.destroy();
        ClientManager.remove(proxy, version, channelClient);
        log.info(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // send heartbeat packet
            sendHeartBeat(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void sendHeartBeat(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        HeartbeatPacket heartbeatPacket = new HeartbeatPacket();
        channel.writeAndFlush(heartbeatPacket).addListener(future -> {
            if (future.isSuccess()) {
                // clear fail mark
                channelClient.clearFailMark();
            } else {
                int failMark = channelClient.getFailMark();
                if (failMark >= 3) {
                    channelClient.destroy();
                    ClientManager.remove(proxy, version, channelClient);
                    log.error(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());

                } else {
                    channelClient.addFailMark();
                    channelClient.reconnect();
                }
            }
        });
    }
}