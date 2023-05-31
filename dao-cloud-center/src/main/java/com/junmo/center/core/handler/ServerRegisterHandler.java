package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.core.model.ClusterSyncServerModel;
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
public class ServerRegisterHandler extends SimpleChannelInboundHandler<RegisterProviderModel> {

    private RegisterProviderModel registerProviderModel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProviderModel registerProviderModel) {
        RegisterCenterManager.register(registerProviderModel);
        this.registerProviderModel = registerProviderModel;
        // todo cluster all node
        ctx.writeAndFlush(new HeartbeatPacket()).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("<<<<<<<<<< back server heartbeat fail {} >>>>>>>>>>", ctx.channel(), f.cause());
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (registerProviderModel != null) {
            RegisterCenterManager.delete(registerProviderModel);
            ClusterSyncServerModel clusterSyncServerModel = new ClusterSyncServerModel();
            clusterSyncServerModel.setFlag((byte) -1);
            clusterSyncServerModel.setRegisterProviderModel(registerProviderModel);
            // sync server to other center
            CenterClusterManager.SyncServerHandler.notice(clusterSyncServerModel);
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (registerProviderModel != null) {
                RegisterCenterManager.delete(registerProviderModel);
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
