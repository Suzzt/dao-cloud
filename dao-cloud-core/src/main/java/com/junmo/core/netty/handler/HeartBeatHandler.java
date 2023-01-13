package com.junmo.core.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author: sucf
 * @date: 2023/1/7 22:59
 * @description:
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        IdleState state = event.state();
        listen(ctx, state);
    }

    /**
     * this method must override it
     *
     * @param ctx
     * @param state
     * @throws Exception
     */
    public void listen(ChannelHandlerContext ctx, IdleState state) throws Exception {
        throw new Exception();
    }
}
