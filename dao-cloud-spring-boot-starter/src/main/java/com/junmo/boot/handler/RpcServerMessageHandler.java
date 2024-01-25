package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.ServiceManager;
import com.junmo.boot.bootstrap.unit.ServiceInvoker;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcServerMessageHandler extends SimpleChannelInboundHandler<RpcRequestModel> {
    /**
     * rpc do invoke thread pool
     */
    private ThreadPoolExecutor serverHandlerThreadPool;

    public RpcServerMessageHandler(ThreadPoolExecutor serverHandlerThreadPool) {
        this.serverHandlerThreadPool = serverHandlerThreadPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel rpcRequestModel) {
        // do invoke service
        serverHandlerThreadPool.execute(() -> {
            // invoke + response
            ServiceInvoker serviceInvoker = ServiceManager.getServiceInvoker(rpcRequestModel.getProvider(), rpcRequestModel.getVersion());
            RpcResponseModel responseModel = serviceInvoker.doInvoke(rpcRequestModel);
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.SERVICE_RPC_RESPONSE_MESSAGE, serviceInvoker.getSerialized(), responseModel);
            ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<< send rpc result data error >>>>>>>>>>", future.cause());
                }
            });
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info(">>>>>>>>>> close the client {} <<<<<<<<<<", ctx.channel());
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
