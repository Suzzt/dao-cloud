package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.CenterClusterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.model.CallTrendModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/7/14 16:23
 * Receive call trend handler
 */
@Slf4j
public class ReceiveCallTrendHandler extends SimpleChannelInboundHandler<CallTrendModel> {

    private final RegisterCenterManager registerCenterManager;

    public ReceiveCallTrendHandler(RegisterCenterManager registerCenterManager) {
        this.registerCenterManager = registerCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CallTrendModel model) throws Exception {
        registerCenterManager.callTrendIncrement(model);
        // notice cluster all node
        CenterClusterManager.syncCallTrendToCluster(SyncClusterInformationRequestHandler.CALL_TREND_INCREMENT, model);
    }
}
