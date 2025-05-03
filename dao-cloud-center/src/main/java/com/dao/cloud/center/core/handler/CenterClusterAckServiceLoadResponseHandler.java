package com.dao.cloud.center.core.handler;

import com.dao.cloud.core.model.AckServiceLoadResponseModel;
import com.dao.cloud.core.util.LongPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @date 2025/5/2 21:57
 * @since 1.0.0
 */
@Slf4j
public class CenterClusterAckServiceLoadResponseHandler extends SimpleChannelInboundHandler<AckServiceLoadResponseModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AckServiceLoadResponseModel model) {
        int loadedNumber = model.getNumber();
        Promise<Object> promise = LongPromiseBuffer.getInstance().remove(model.getSequenceId());
        promise.setSuccess(loadedNumber);
    }
}