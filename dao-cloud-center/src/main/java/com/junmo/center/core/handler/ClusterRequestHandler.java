package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.core.model.HeartbeatModel;
import com.junmo.core.netty.protocol.HeartbeatPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author: sucf
 * @date: 2023/4/16 23:03
 * @description: receive cluster heartbeat handler
 */
@Slf4j
public class ClusterRequestHandler extends SimpleChannelInboundHandler<HeartbeatModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatModel msg) {
        log.info(">>>>>>>>>>> receive cluster (connect address = {}) heart beat packet <<<<<<<<<<<", ctx.channel().remoteAddress());
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        CenterClusterManager.joinCluster(clientIP);
        ctx.channel().writeAndFlush(new HeartbeatPacket()).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< send back cluster (connect address = {}) heart beat packet error >>>>>>>>>>>", ctx.channel().remoteAddress(), future.cause());
            }
        });
    }
}