package com.junmo.boot.handler;

import com.junmo.core.enums.CodeEnum;
import com.junmo.core.model.GlobalExceptionModel;
import com.junmo.core.model.Model;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/2/20 15:45
 * @description: netty global trigger exception handler.
 * 这个全局异常处理器只能针对没重写exceptionCaught handler的统一处理
 * important: 对于那些重要的、异步promise、有请求序列id的请求一定要在对应处理器上重写exceptionCaught方法
 */
@Slf4j
public class NettyGlobalTriggerExceptionHandler extends SimpleChannelInboundHandler<Model> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Model model) throws Exception {
        ctx.fireChannelRead(model);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO 这里最好打印下请求信息
        log.error("An unknown error occurred in the component, which is a bad sign", cause);
        GlobalExceptionModel exceptionModel = new GlobalExceptionModel();
        exceptionModel.setErrorCode(CodeEnum.SERVICE_UNKNOWN_ERROR.getCode());
        exceptionModel.setErrorMessage(CodeEnum.SERVICE_UNKNOWN_ERROR.getText());
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.GLOBAL_DAO_EXCEPTION_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, exceptionModel);
        ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<< Global exception send data error >>>>>>>>>>", future.cause());
            }
        });
    }
}
