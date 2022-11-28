package com.junmo.config.netty.handler;

import com.junmo.config.register.CoreRegister;
import com.junmo.core.enums.Constant;
import com.junmo.core.model.DaoMessage;
import com.junmo.core.model.ServerRegisterModel;
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
public class ServerRegisterMessageHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) {
        DaoMessage daoMessage;
        if(obj instanceof ServerRegisterModel){
            ServerRegisterModel serverRegisterModel = (ServerRegisterModel) obj;
            CoreRegister.add(serverRegisterModel.getProxyName(), serverRegisterModel.getIpAddress());
            daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, true);
        }else{

            daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, obj);
        }
        ctx.writeAndFlush(daoMessage);
    }

}
