package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import model.RpcResponseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseModel> {

    public static final Map<Long, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseModel msg) throws Exception {
        log.debug("{}", msg);
        // get promise
        Promise<Object> promise = PROMISE_MAP.get(msg.getSequenceId());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if(exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}