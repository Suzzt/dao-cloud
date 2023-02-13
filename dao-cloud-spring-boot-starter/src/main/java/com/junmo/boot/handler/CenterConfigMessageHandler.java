package com.junmo.boot.handler;

import com.junmo.core.model.ConfigModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2023/2/12 16:58
 * @description:
 */
public class CenterConfigMessageHandler extends SimpleChannelInboundHandler<ConfigModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigModel configModel) throws Exception {
    }
}
