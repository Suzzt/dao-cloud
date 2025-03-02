package com.dao.cloud.core.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/4/2 13:58
 * Unite Print Exception Handler
 */
@Slf4j
public class PrintExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("An exception occurred in link={} processing.", ctx.channel(), cause);
    }
}
