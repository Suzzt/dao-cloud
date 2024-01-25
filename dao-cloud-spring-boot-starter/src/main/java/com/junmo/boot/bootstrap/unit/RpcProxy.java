package com.junmo.boot.bootstrap.unit;

import cn.hutool.core.util.IdUtil;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.LongPromiseBuffer;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2022/10/28 22:30
 * @description: rpc build proxy
 */
@Slf4j
public class RpcProxy {

    /**
     * build proxy bean
     *
     * @param serviceClass
     * @param proxyProviderModel
     * @param daoLoadBalance
     * @param timeout
     * @param <T>
     * @return
     */
    public static <T> T build(Class<T> serviceClass, ProxyProviderModel proxyProviderModel, byte serialized, DaoLoadBalance daoLoadBalance, long timeout) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ProxyHandler(proxyProviderModel, serialized, daoLoadBalance, timeout));
    }

    /**
     * proxy rpc handler
     */
    static class ProxyHandler implements InvocationHandler {

        private ProxyProviderModel proxyProviderModel;

        private byte serialized;

        private DaoLoadBalance daoLoadBalance;

        private long timeout;

        public ProxyHandler(ProxyProviderModel proxyProviderModel, byte serialized, DaoLoadBalance daoLoadBalance, long timeout) {
            this.proxyProviderModel = proxyProviderModel;
            this.serialized = serialized;
            this.daoLoadBalance = daoLoadBalance;
            this.timeout = timeout;
        }

        @Override
        public Object invoke(Object obj, Method method, Object[] args) throws InterruptedException {
            ProviderModel providerModel = proxyProviderModel.getProviderModel();

            //todo 这里要注意分布式下
            long sequenceId = IdUtil.getSnowflake(2, 2).nextId();
            RpcRequestModel requestModel = new RpcRequestModel(
                    sequenceId,
                    providerModel.getProvider(),
                    providerModel.getVersion(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
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
            DaoMessage message = new DaoMessage((byte) 1, MessageType.SERVICE_RPC_REQUEST_MESSAGE, serialized, requestModel);

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
                throw new DaoException(promise.cause());
            }
        }
    }
}
