package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.model.CallTrendModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2024/7/14 16:23
 * @description: Receive call trend handler
 */
public class ReceiveCallTrendHandler extends SimpleChannelInboundHandler<CallTrendModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CallTrendModel model) throws Exception {
    }
}
