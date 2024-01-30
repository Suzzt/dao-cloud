package com.junmo.boot.handler;

import com.junmo.core.model.GatewayRequestModel;
import com.junmo.core.model.RpcRequestModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2024/1/30 11:24
 * @description: 网关请求处理响应, 该请求会打到rpc服务调用的handler上
 */
public class GatewayServiceMessageHandler extends SimpleChannelInboundHandler<GatewayRequestModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayRequestModel gatewayRequestModel) {
        RpcRequestModel rpcRequestModel = wrapper(gatewayRequestModel);
        // 将加工后的对象传递到下一个rpc处理器
        ctx.fireChannelRead(rpcRequestModel);
    }

    /**
     * 包装处理网关请求到服务调用请求
     *
     * @param gatewayRequestModel
     * @return
     */
    private RpcRequestModel wrapper(GatewayRequestModel gatewayRequestModel) {
        return null;
    }
}
