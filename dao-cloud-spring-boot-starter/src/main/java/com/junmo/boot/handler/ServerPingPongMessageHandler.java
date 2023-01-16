package com.junmo.boot.handler;

import com.junmo.boot.channel.ChannelClient;
import com.junmo.core.enums.Constant;
import com.junmo.core.model.PingPongModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/1/15 12:29
 * @description:
 */
@Slf4j
public class ServerPingPongMessageHandler extends SimpleChannelInboundHandler<PingPongModel> {

    private ChannelClient channelClient;

    public ServerPingPongMessageHandler(ChannelClient channelClient) {
        this.channelClient = channelClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingPongModel pingPongModel) {
        channelClient.setState(Constant.CHANNEL_ALIVE_CONNECT_STATE);
        log.info(">>>>>>>>>>>server (ip = {},port = {}) heart beat ping-pong <<<<<<<<<<<", ctx.channel().remoteAddress(), null);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelClient.setState(Constant.CHANNEL_ALIVE_DISCONNECT_STATE);
        super.channelUnregistered(ctx);
    }
}