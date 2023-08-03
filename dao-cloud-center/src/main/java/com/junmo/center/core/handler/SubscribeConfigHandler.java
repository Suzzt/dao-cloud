package com.junmo.center.core.handler;

import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.ConfigChannelManager;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2023/2/26 17:49
 * @description: subscribe config handler
 */
@Slf4j
public class SubscribeConfigHandler extends SimpleChannelInboundHandler<ProxyConfigModel> {

    private ProxyConfigModel proxyConfigModel;

    private ConfigCenterManager configCenterManager;

    public SubscribeConfigHandler(ConfigCenterManager configCenterManager){
        this.configCenterManager = configCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyConfigModel proxyConfigModel) {
        ConfigChannelManager.add(proxyConfigModel, ctx.channel());
        String configValue = configCenterManager.getConfigValue(proxyConfigModel);
        this.proxyConfigModel = proxyConfigModel;
        ConfigModel configModel = new ConfigModel();
        configModel.setProxyConfigModel(proxyConfigModel);
        configModel.setConfigValue(configValue);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configModel);
        ctx.writeAndFlush(daoMessage).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("<<<<<<<<<< pull config value fail. proxyConfigModel={}, channel={} >>>>>>>>>>", proxyConfigModel, ctx.channel(), f.cause());
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (proxyConfigModel != null) {
            ConfigChannelManager.remove(proxyConfigModel, ctx.channel());
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                if (proxyConfigModel != null) {
                    ConfigChannelManager.remove(proxyConfigModel, ctx.channel());
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("<<<<<<<<<< send message error {} >>>>>>>>>>", ctx.channel(), cause);
        ctx.fireExceptionCaught(cause);
    }
}
