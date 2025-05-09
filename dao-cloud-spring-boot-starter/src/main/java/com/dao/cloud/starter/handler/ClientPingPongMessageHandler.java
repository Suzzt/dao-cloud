package com.dao.cloud.starter.handler;

import com.dao.cloud.starter.unit.Client;
import com.dao.cloud.core.model.HeartbeatModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/7/20 23:26
 * 接收服务端ping pong
 */
@Slf4j
public class ClientPingPongMessageHandler extends SimpleChannelInboundHandler<HeartbeatModel> {

    private Client client;

    public ClientPingPongMessageHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatModel heartbeatModel) {
        log.debug(">>>>>>>>>>> receive client (connect address = {}) heart beat packet <<<<<<<<<<<", ctx.channel().remoteAddress());
        // clear fail mark. 接受服务端返回的心跳,归置客户端发送次数
        client.clearFailMark();
    }
}
