package com.junmo.center.core.handler;

import com.junmo.center.core.CenterClusterManager;
import com.junmo.core.model.HeartbeatModel;
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
public class AcceptHeartbeatClusterCenterHandler extends SimpleChannelInboundHandler<HeartbeatModel> {

    private String ip;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatModel msg) {
        // get cluster ip
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        ip = clientIP;
        CenterClusterManager.joinCluster(clientIP);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        CenterClusterManager.remove(ip);
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
