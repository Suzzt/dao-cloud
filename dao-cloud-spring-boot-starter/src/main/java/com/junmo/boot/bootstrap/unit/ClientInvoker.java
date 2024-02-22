package com.junmo.boot.bootstrap.unit;

import cn.hutool.core.util.IdUtil;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.*;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.LongPromiseBuffer;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2024/1/27 18:07
 * @description: client invoke handler
 */
@Slf4j
public class ClientInvoker {

    private ProxyProviderModel proxyProviderModel;

    private DaoLoadBalance daoLoadBalance;

    private byte serializable;

    /**
     * Service call timeout. The unit is seconds
     */
    private long timeout;

    public ClientInvoker(ProxyProviderModel proxyProviderModel, DaoLoadBalance daoLoadBalance, byte serializable, long timeout) {
        this.proxyProviderModel = proxyProviderModel;
        this.daoLoadBalance = daoLoadBalance;
        this.serializable = serializable;
        this.timeout = timeout;
    }

    public Object invoke(GatewayRequestModel gatewayRequestModel) throws InterruptedException {
        return doInvoke(gatewayRequestModel, MessageType.GATEWAY_RPC_REQUEST_MESSAGE);
    }

    public Object invoke(RpcRequestModel rpcRequestModel) throws InterruptedException {
        return doInvoke(rpcRequestModel, MessageType.SERVICE_RPC_REQUEST_MESSAGE);
    }

    public Object doInvoke(ServiceRequestModel model, byte messageType) throws InterruptedException {
        long sequenceId = IdUtil.getSnowflake(2, 2).nextId();
        model.setSequenceId(sequenceId);
        // get client channel
        Client client;
        while (true) {
            // 把出错的几率降到最低,选出合适的channel
            Set<ServerNodeModel> providerNodes = ClientManager.getProviderNodes(proxyProviderModel);
            Set<Client> clients = ClientManager.getSharedClient(providerNodes);
            if (CollectionUtils.isEmpty(clients)) {
                throw new DaoException("proxy = '" + proxyProviderModel.getProxy() + "', provider = '" + proxyProviderModel.getProviderModel() + "' no provider server");
            }
            // load balance
            client = daoLoadBalance.route(clients);
            if (client.getChannel().isActive()) {
                break;
            }
            ClientManager.remove(new ServerNodeModel(client.getIp(), client.getPort()));
        }
        DaoMessage message = new DaoMessage((byte) 1, messageType, serializable, model);

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
        if (!promise.await(timeout * 1_000)) {
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
