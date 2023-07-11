package com.junmo.center.core.handler;

import com.junmo.core.exception.DaoException;
import com.junmo.core.model.NumberingModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.springframework.util.StringUtils;

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
        if (StringUtils.hasLength(numberingModel.getErrorMessage())) {
            promise.setFailure(new DaoException(numberingModel.getErrorMessage()));
        } else {
            promise.setSuccess(numberingModel.getSequenceId());
        }
    }
}
