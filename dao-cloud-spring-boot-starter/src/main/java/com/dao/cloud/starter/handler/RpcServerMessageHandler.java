package com.dao.cloud.starter.handler;

import cn.hutool.json.JSONUtil;
import com.dao.cloud.starter.bootstrap.manager.ServiceManager;
import com.dao.cloud.starter.bootstrap.unit.ServiceInvoker;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.HttpServletResponse;
import com.dao.cloud.core.model.RpcRequestModel;
import com.dao.cloud.core.model.RpcResponseModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.timeout.IdleStateEvent;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

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
            this.dealHttpResponseValue(rpcRequestModel, responseModel);
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

    private void dealHttpResponseValue(RpcRequestModel rpcRequestModel, RpcResponseModel responseModel) {

        if(!rpcRequestModel.isHttp()) {
            return;
        }

        HttpServletResponse httpServletResponse = rpcRequestModel.getHttpServletResponse();
        Object result = responseModel.getReturnValue();

        // 框架作者必须传入有效的返回参数
        if (Objects.isNull(httpServletResponse)) {
            throw new DaoException("框架错误！http返回值必须是Response");
        }

        byte[] bodyData = httpServletResponse.getBodyData();
        // 如果用户没有主动设置body数据，那么默认为返回json数据，将返回值进行序列化
        if (Objects.isNull(bodyData)) {
            // 默认返回json
            bodyData = Objects.isNull(result) ? null : JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8);
            httpServletResponse.setBodyData(bodyData);
        }
        httpServletResponse.addHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Objects.isNull(bodyData)
            ? "0"
            : bodyData.length + "");

        responseModel.setReturnValue(httpServletResponse);
    }

}
