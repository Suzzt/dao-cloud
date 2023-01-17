package com.junmo.config.register.handler;

import com.junmo.config.register.Register;
import com.junmo.core.model.RegisterServerModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.model.RegisterModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description: server register handler
 */
@Slf4j
public class ServerRegisterMessageHandler extends SimpleChannelInboundHandler<RegisterModel> {

    private String proxy;

    private String ipLinkPort;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterModel registerModel) {
        String proxy = registerModel.getProxy();
        String ipLinkPort = registerModel.getIpLinkPort();
        Register.register(proxy, ipLinkPort);
        this.proxy = proxy;
        this.ipLinkPort = ipLinkPort;
        List<ServerNodeModel> serverNodeModels = Register.getServers(proxy);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.REGISTRY_RESPONSE_MESSAGE, (byte) 0, new RegisterServerModel(proxy, serverNodeModels));
        ctx.writeAndFlush(daoMessage).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("{}", ctx, f.cause());
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Register.delete(proxy, ipLinkPort);
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{}", ctx, cause);
    }
}
