package com.junmo.boot.handler;

import com.junmo.boot.registry.ServerManager;
import com.junmo.core.enums.Constant;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.DaoMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
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

    private ServerManager serverManager;

    public RpcRequestMessageHandler(ThreadPoolExecutor serverHandlerThreadPool, ServerManager serverManager) {
        this.serverHandlerThreadPool = serverHandlerThreadPool;
        this.serverManager = serverManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel rpcRequestModel) {
        // do invoke service
        serverHandlerThreadPool.execute(() -> {
            // invoke + response
            RpcResponseModel responseModel = serverManager.doInvoke(rpcRequestModel);
            DaoMessage daoMessage = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, responseModel);
            ctx.writeAndFlush(daoMessage);
        });
    }

}
