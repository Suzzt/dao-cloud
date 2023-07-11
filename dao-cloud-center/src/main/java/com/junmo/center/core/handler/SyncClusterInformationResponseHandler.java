package com.junmo.center.core.handler;

import com.junmo.core.model.NumberingModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2023/7/11 18:23
 * @description: 回应cluster数据同步
 */
public class SyncClusterInformationResponseHandler extends SimpleChannelInboundHandler<NumberingModel> {

    public static final Map<Long, Promise> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NumberingModel numberingModel) throws Exception {
        Promise promise = PROMISE_MAP.remove(numberingModel.getSequenceId());
        promise.setSuccess(numberingModel.getSequenceId());
    }
}
