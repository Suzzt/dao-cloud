package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.LogManager;
import com.dao.cloud.core.model.LogModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2024/8/23 16:58
 * @description: log receive
 */
public class LogHandler extends SimpleChannelInboundHandler<LogModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogModel msg) throws Exception {
        // collect log
        LogManager.collect(msg);
    }
}
