package com.junmo.center.register.handler;

import com.junmo.center.register.RegisterManager;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.netty.protocol.HeartbeatPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: server register handler
 */
@Slf4j
public class ServerRegisterMessageHandler extends SimpleChannelInboundHandler<RegisterProviderModel> {

    private RegisterProviderModel registerProviderModel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProviderModel registerProviderModel) {
        RegisterManager.register(registerProviderModel);
        this.registerProviderModel = registerProviderModel;
        ctx.writeAndFlush(new HeartbeatPacket()).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("<<<<<<<<<< back server heartbeat fail {} >>>>>>>>>>", ctx.channel(), f.cause());
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (registerProviderModel != null) {
            RegisterManager.delete(registerProviderModel);
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (registerProviderModel != null) {
                RegisterManager.delete(registerProviderModel);
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("<<<<<<<<<< register error {} >>>>>>>>>>", ctx.channel(), cause);
    }
}
