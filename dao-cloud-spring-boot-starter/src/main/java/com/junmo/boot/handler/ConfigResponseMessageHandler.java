package com.junmo.boot.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2022/11/19 09:11
 * @description:
 */
@Slf4j
public class ConfigResponseMessageHandler extends SimpleChannelInboundHandler<Boolean> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Boolean msg) {
        log.debug("======✓✓✓✓✓✓heart beat dao-cloud-config bingo✓✓✓✓✓✓======");
    }
}