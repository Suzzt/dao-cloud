package com.junmo.config.register.handler;

import com.junmo.config.register.RegisterManager;
import com.junmo.core.enums.Constant;
import com.junmo.core.model.ServerRegisterModel;
import com.junmo.core.netty.protocol.DaoMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: server register handler
 */
@Slf4j
public class ServerRegisterMessageHandler extends SimpleChannelInboundHandler<ServerRegisterModel> {

    private String proxy;

    private String ipLinkPort;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerRegisterModel serverRegisterModel) {
        String proxy = serverRegisterModel.getProxy();
        String ipLinkPort = serverRegisterModel.getIpLinkPort();
        RegisterManager.register(proxy, ipLinkPort);
        this.proxy = proxy;
        this.ipLinkPort = ipLinkPort;
        DaoMessage daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, true);
        ctx.writeAndFlush(daoMessage);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        RegisterManager.delete(proxy, ipLinkPort);
        super.channelUnregistered(ctx);
    }
}
