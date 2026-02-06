package com.dao.cloud.starter.unit;

import cn.hutool.core.util.IdUtil;
import com.dao.cloud.core.constant.Protocol;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.LongPromiseBuffer;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.manager.ClientManager;
import com.dao.cloud.starter.utils.HashKeyExtractor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author sucf
 * @date 2024/1/27 18:07
 * client invoke handler
 * @since 1.0.0
 */
@Slf4j
public class ClientInvoker {

    private final ProxyProviderModel proxyProviderModel;

    private final DaoLoadBalance daoLoadBalance;

    private final byte serializable;

    /**
     * Service call timeout. The unit is seconds
     */
    private final long timeout;

    public ClientInvoker(ProxyProviderModel proxyProviderModel, DaoLoadBalance daoLoadBalance, byte serializable, long timeout) {
        this.proxyProviderModel = proxyProviderModel;
        this.daoLoadBalance = daoLoadBalance;
        this.serializable = serializable;
        this.timeout = timeout;
    }

    public Object invoke(GatewayRequestModel gatewayRequestModel) throws InterruptedException {
        return doInvoke(gatewayRequestModel, MessageType.GATEWAY_RPC_REQUEST_MESSAGE, null, null);
    }

    public Object invoke(RpcRequestModel rpcRequestModel, Method method, Object[] methodArgs) throws InterruptedException {
        return doInvoke(rpcRequestModel, MessageType.SERVICE_RPC_REQUEST_MESSAGE, method, methodArgs);
    }

    public Object doInvoke(ServiceRequestModel model, byte messageType, Method method, Object[] methodArgs) throws InterruptedException {
        long sequenceId = IdUtil.getSnowflake(2, 2).nextId();
        model.setSequenceId(sequenceId);
        if (!StringUtils.hasLength(MDC.get("traceId"))) {
            MDC.put("traceId", IdUtil.getSnowflake(2, 2).nextIdStr());
        }
        model.setTraceId(MDC.get("traceId"));
        // get client channel
        Client client;
        while (true) {
            // 把出错的几率降到最低,选出合适的channel
            Set<ServerNodeModel> serverNodeModels = ClientManager.getAvailableProviderNodes(proxyProviderModel);
            if (CollectionUtils.isEmpty(serverNodeModels)) {
                log.error("proxy = '{}', provider = '{}' no provider server", proxyProviderModel.getProxy(), proxyProviderModel.getProviderModel());
                if (messageType == MessageType.SERVICE_RPC_REQUEST_MESSAGE) {
                    throw new DaoException(CodeEnum.SERVICE_PROVIDER_NOT_EXIST);
                } else {
                    throw new DaoException(CodeEnum.GATEWAY_SERVICE_NOT_EXIST);
                }
            }
            Set<Client> clients = ClientManager.getSharedClient(serverNodeModels);
            if (CollectionUtils.isEmpty(clients)) {
                log.error("proxy = '{}', provider = '{}' no provider server", proxyProviderModel.getProxy(), proxyProviderModel.getProviderModel());
                if (messageType == MessageType.SERVICE_RPC_REQUEST_MESSAGE) {
                    throw new DaoException(CodeEnum.SERVICE_PROVIDER_NOT_EXIST);
                } else {
                    throw new DaoException(CodeEnum.GATEWAY_SERVICE_NOT_EXIST);
                }
            }
            // load balance with hash key support
            Object hashKeyValue = null;
            if (method != null && methodArgs != null) {
                hashKeyValue = HashKeyExtractor.extractHashKey(method, methodArgs);
            }
            client = daoLoadBalance.route(clients, hashKeyValue);
            if (client.getChannel().isActive()) {
                break;
            }
            ClientManager.remove(new ServerNodeModel(client.getIp(), client.getPort()));
        }
        DaoMessage message = new DaoMessage(Protocol.DEFAULT_VERSION, messageType, serializable, model);

        // 异步执行！ promise 对象来处理异步接收的结果线程
        Promise<Object> promise = new DefaultPromise<>(client.getChannel().eventLoop());
        LongPromiseBuffer.getInstance().put(sequenceId, promise);

        // push message
        client.getChannel().writeAndFlush(message).addListener(future -> {
            if (!future.isSuccess()) {
                LongPromiseBuffer.getInstance().remove(sequenceId);
                promise.setFailure(future.cause());
                log.error("<<<<<<<<<< send rpc do invoke message error >>>>>>>>>>", future.cause());
            }
        });

        // 等待 promise 结果
        if (!promise.await(timeout)) {
            LongPromiseBuffer.getInstance().remove(sequenceId);
            throw new DaoException("rpc do invoke time out");
        }
        if (promise.isSuccess()) {
            client.clearFailMark();
            return promise.getNow();
        } else {
            throw (DaoException) promise.cause();
        }
    }
}
