package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.LogManager;
import com.dao.cloud.core.model.LogModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author sucf
 * @since 1.0
 * log receive
 */
public class LogHandler extends SimpleChannelInboundHandler<LogModel> {

    private LogManager logManager;

    public LogHandler(LogManager logManager) {
        this.logManager = logManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogModel msg) throws Exception {
        // collect log
        logManager.collect(msg);
    }
}
