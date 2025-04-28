package com.dao.cloud.starter.handler;

import cn.hutool.json.JSONUtil;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.DaoCloudServletResponse;
import com.dao.cloud.core.model.RpcRequestModel;
import com.dao.cloud.core.model.RpcResponseModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.log.LogHandlerInterceptor;
import com.dao.cloud.starter.manager.ServiceManager;
import com.dao.cloud.starter.unit.ServiceInvoker;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/10/29 10:28
 */
@Slf4j
public class RpcServerMessageHandler extends SimpleChannelInboundHandler<RpcRequestModel> {
    /**
     * rpc do invoke thread pool
     */
    private ThreadPoolExecutor serverHandlerThreadPool;

    private LogHandlerInterceptor logHandlerInterceptor;

    public RpcServerMessageHandler(ThreadPoolExecutor serverHandlerThreadPool, LogHandlerInterceptor logHandlerInterceptor) {
        this.serverHandlerThreadPool = serverHandlerThreadPool;
        this.logHandlerInterceptor = logHandlerInterceptor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel rpcRequestModel) {
        // do invoke service
        serverHandlerThreadPool.execute(() -> {
            logHandlerInterceptor.enter(rpcRequestModel.getTraceId());
            // invoke + response
            ServiceInvoker serviceInvoker = ServiceManager.getServiceInvoker(rpcRequestModel.getProvider(), rpcRequestModel.getVersion());
            RpcResponseModel responseModel = serviceInvoker.doInvoke(rpcRequestModel);
            this.dealHttpResponseValue(rpcRequestModel, responseModel);
            DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.SERVICE_RPC_RESPONSE_MESSAGE, serviceInvoker.getSerialized(), responseModel);
            ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<< send rpc result data error >>>>>>>>>>", future.cause());
                }
            });
            logHandlerInterceptor.leave();
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

        if (!rpcRequestModel.isHttp()) {
            return;
        }

        DaoCloudServletResponse daoCloudServletResponse = rpcRequestModel.getDaoCloudServletResponse();
        Object result = responseModel.getReturnValue();

        // 框架作者必须传入有效的返回参数
        if (Objects.isNull(daoCloudServletResponse)) {
            throw new DaoException("框架错误！http返回值必须是Response");
        }

        byte[] bodyData = daoCloudServletResponse.getBodyData();
        // 如果用户没有主动设置body数据，那么默认为返回json数据，将返回值进行序列化
        if (Objects.isNull(bodyData)) {
            // 默认返回json
            bodyData = Objects.isNull(result) ? null : JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8);
            daoCloudServletResponse.setBodyData(bodyData);
        }
        daoCloudServletResponse.addHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Objects.isNull(bodyData) ? "0" : bodyData.length + "");

        responseModel.setReturnValue(daoCloudServletResponse);
    }

}
