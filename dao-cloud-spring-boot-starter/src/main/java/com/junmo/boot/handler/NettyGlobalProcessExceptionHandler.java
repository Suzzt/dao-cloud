package com.junmo.boot.handler;

import com.junmo.core.model.GlobalExceptionModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/2/20 23:31
 * @description:
 */
@Slf4j
public class NettyGlobalProcessExceptionHandler extends SimpleChannelInboundHandler<GlobalExceptionModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GlobalExceptionModel exceptionModel) throws Exception {

    }
}
