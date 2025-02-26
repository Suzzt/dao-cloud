package com.dao.cloud.starter.handler;

import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.GlobalExceptionModel;
import com.dao.cloud.core.model.Model;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 * netty global trigger exception handler.
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
        Model model = ctx.channel().attr(DaoCloudConstant.REQUEST_MESSAGE_ATTR_KEY).get();
        log.error("An unknown error occurred in the component, which is a bad sign. the message is: {}", model, cause);
        GlobalExceptionModel exceptionModel = new GlobalExceptionModel();
        exceptionModel.setDaoException(new DaoException(CodeEnum.SERVICE_UNKNOWN_ERROR));
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.GLOBAL_DAO_EXCEPTION_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, exceptionModel);
        ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<< Global exception send data error >>>>>>>>>>", future.cause());
            }
        });
    }
}
