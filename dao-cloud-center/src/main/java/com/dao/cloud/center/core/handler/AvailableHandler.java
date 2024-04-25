package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.Model;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2024/4/25 22:54
 * @description: Node available handler
 */
public class AvailableHandler extends SimpleChannelInboundHandler<Model> {

    private boolean available;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Model msg) throws Exception {
        if (!available) {
            // todo 这里要封装一个消息异常给到请求端
            throw new DaoException("The current node is not ready");
        }
        ctx.fireChannelRead(msg);
    }

    public void ready() {
        this.available = true;
    }
}
