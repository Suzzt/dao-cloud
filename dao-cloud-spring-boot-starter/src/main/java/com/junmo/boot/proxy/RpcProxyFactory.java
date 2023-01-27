package com.junmo.boot.proxy;

import cn.hutool.core.util.IdUtil;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.handler.RpcResponseMessageHandler;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import io.netty.channel.Channel;
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
 * @description: rpc proxy factory
 */
@Slf4j
public class RpcProxyFactory {

    /**
     * @param serviceClass
     * @param proxy
     * @param daoLoadBalance
     * @param timeout
     * @param <T>
     * @return
     */
    public static <T> T build(Class<T> serviceClass, String proxy, DaoLoadBalance daoLoadBalance, long timeout) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ProxyHandler(serviceClass, proxy, daoLoadBalance, timeout));
    }

    static class ProxyHandler implements InvocationHandler {

        private Class<?> serviceClass;

        private String proxy;

        private DaoLoadBalance daoLoadBalance;

        private long timeout;

        public ProxyHandler(Class<?> serviceClass, String proxy, DaoLoadBalance daoLoadBalance, long timeout) {
            this.serviceClass = serviceClass;
            this.proxy = proxy;
            this.daoLoadBalance = daoLoadBalance;
            this.timeout = timeout;
        }

        @Override
        public Object invoke(Object obj, Method method, Object[] args) throws InterruptedException {

            //todo 这里要注意分布式下
            long sequenceId = IdUtil.getSnowflake(2, 2).nextId();
            RpcRequestModel requestModel = new RpcRequestModel(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // load balance
            Channel channel;
            while (true) {
                // 把出错的几率降到最低,选出合适的channel
                Set<ChannelClient> channelClients = ClientManager.getClients(proxy);
                if (CollectionUtils.isEmpty(channelClients)) {
                    throw new DaoException("proxy = '" + proxy + "' no server provider");
                }
                ChannelClient channelClient = daoLoadBalance.route(channelClients);
                channel = channelClient.getChannel();
                if (channel.isActive()) {
                    break;
                }
                ClientManager.remove(proxy, channelClient);
            }
            DaoMessage message = new DaoMessage((byte) 1, MessageModelTypeManager.RPC_REQUEST_MESSAGE, DaoCloudProperties.serializerType, requestModel);
            // push message
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    Promise<Object> promise = RpcResponseMessageHandler.PROMISE_MAP.remove(sequenceId);
                    promise.setFailure(future.cause());
                    log.error("<<<<<<<<<< send rpc do invoke message error >>>>>>>>>>", future.cause());
                }
            });

            // 异步！ promise 对象来处理异步接收的结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            RpcResponseMessageHandler.PROMISE_MAP.put(sequenceId, promise);

            //等待 promise 结果
            if (!promise.await(timeout)) {
                throw new DaoException("rpc do invoke time out");
            }
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new DaoException(promise.cause());
            }
        }
    }

}
