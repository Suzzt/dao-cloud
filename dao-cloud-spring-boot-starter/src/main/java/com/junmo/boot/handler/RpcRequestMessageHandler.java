package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.RpcServerBootstrap;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestModel> {
    /**
     * thread pool
     */
    private ThreadPoolExecutor serverHandlerThreadPool;

    private RpcServerBootstrap rpcServerBootstrap;

    public RpcRequestMessageHandler(ThreadPoolExecutor serverHandlerThreadPool, RpcServerBootstrap rpcServerBootstrap) {
        this.serverHandlerThreadPool = serverHandlerThreadPool;
        this.rpcServerBootstrap = rpcServerBootstrap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel rpcRequestModel) {
        // do invoke service
        serverHandlerThreadPool.execute(() -> {
            // invoke + response
            RpcResponseModel responseModel = rpcServerBootstrap.doInvoke(rpcRequestModel);
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageModelTypeManager.RPC_RESPONSE_MESSAGE, DaoCloudProperties.serializerType, responseModel);
            ctx.writeAndFlush(daoMessage);
        });
    }

}