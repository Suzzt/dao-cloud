package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.core.model.CallTrendModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/7/14 16:23
 * @description: Receive call trend handler
 */
@Slf4j
public class ReceiveCallTrendHandler extends SimpleChannelInboundHandler<CallTrendModel> {

    private final Persistence persistence;

    public ReceiveCallTrendHandler(Persistence persistence) {
        this.persistence = persistence;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CallTrendModel model) throws Exception {
        persistence.callIncrement(model.getProxyProviderModel(), model.getMethodName(), model.getCount());
    }
}
