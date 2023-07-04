package com.junmo.center.core.handler;

import com.junmo.core.model.FullConfigModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * @author: sucf
 * @date: 2023/7/4 16:01
 * @description:
 */
public class PullConfigResponseHandler extends SimpleChannelInboundHandler<FullConfigModel> {

    public static Promise<FullConfigModel> promise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullConfigModel fullConfigModel) {
        promise.setSuccess(fullConfigModel);
    }
}
