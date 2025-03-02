package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.CenterClusterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.netty.protocol.HeartbeatPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/29 10:28
 * server register handler
 */
@Slf4j
public class ServerRegisterHandler extends SimpleChannelInboundHandler<RegisterProviderModel> {

    private RegisterProviderModel registerProviderModel;

    private RegisterCenterManager registerCenterManager;

    public ServerRegisterHandler(RegisterCenterManager registerCenterManager) {
        this.registerCenterManager = registerCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProviderModel registerProviderModel) {
        registerCenterManager.registry(registerProviderModel);
        this.registerProviderModel = registerProviderModel;
        // notice cluster all node
        CenterClusterManager.syncRegisterToCluster(SyncClusterInformationRequestHandler.UP_SERVER, registerProviderModel);
        ctx.writeAndFlush(new HeartbeatPacket()).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("<<<<<<<<<< back server heartbeat fail {} >>>>>>>>>>", ctx.channel(), f.cause());
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (registerProviderModel != null) {
            registerCenterManager.unregistered(registerProviderModel);
            // sync server to other center
            CenterClusterManager.syncRegisterToCluster(SyncClusterInformationRequestHandler.DOWN_SERVER, registerProviderModel);
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (registerProviderModel != null) {
                registerCenterManager.unregistered(registerProviderModel);
                // sync server to other center
                CenterClusterManager.syncRegisterToCluster(SyncClusterInformationRequestHandler.DOWN_SERVER, registerProviderModel);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
