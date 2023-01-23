package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.channel.ChannelClient;
import com.junmo.core.model.PingPongModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/1/15 12:29
 * @description:
 */
@Slf4j
public class ServerPingPongMessageHandler extends SimpleChannelInboundHandler<PingPongModel> {

    private String proxy;

    private ChannelClient channelClient;

    public ServerPingPongMessageHandler(String proxy, ChannelClient channelClient) {
        this.proxy = proxy;
        this.channelClient = channelClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingPongModel pingPongModel) {
        log.info(">>>>>>>>>>> server (connect address = {}) heart beat ping-pong <<<<<<<<<<<", ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelClient.destroy();
        ClientManager.remove(proxy, channelClient);
        log.info(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object paramObject) throws Exception {
        IdleState state = ((IdleStateEvent) paramObject).state();
        if (state == IdleState.READER_IDLE) {
            channelClient.destroy();
            ClientManager.remove(proxy, channelClient);
            log.info(">>>>>>>>>>> server (connect address = {}) down <<<<<<<<<<<", ctx.channel().remoteAddress());
        }
    }
}