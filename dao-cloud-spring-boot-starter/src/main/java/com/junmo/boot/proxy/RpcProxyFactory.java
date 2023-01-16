package com.junmo.boot.proxy;

import cn.hutool.core.util.IdUtil;
import com.junmo.boot.channel.ChannelClient;
import com.junmo.boot.handler.RpcResponseMessageHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.netty.protocol.DaoMessage;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2022/10/28 22:30
 * @description: 代理工厂
 */
@Slf4j
public class RpcProxyFactory {

    /**
     * build rpc proxy
     *
     * @param serviceClass
     * @param channelClients
     * @param <T>
     * @return
     */
    public static <T> T build(Class<T> serviceClass, Set<ChannelClient> channelClients) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ProxyHandler(serviceClass, channelClients));
    }

    static class ProxyHandler implements InvocationHandler {

        private Class<?> serviceClass;

        private Set<ChannelClient> channelClients;

        public ProxyHandler(Class<?> serviceClass, Set<ChannelClient> channelClients) {
            this.serviceClass = serviceClass;
            this.channelClients = channelClients;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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
            DaoMessage message = new DaoMessage((byte) 1, (byte) 1, (byte) 0, requestModel);
            // push message
            //todo load balance choose server channel
            Iterator<ChannelClient> iterator = channelClients.iterator();
            ChannelClient channelClient = iterator.next();
            Channel channel = channelClient.getChannel();
            channel.writeAndFlush(message);

            // 异步！ promise 对象来处理异步接收的结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            RpcResponseMessageHandler.PROMISE_MAP.put(sequenceId, promise);

            //等待 promise 结果
            promise.await();
            if (promise.isSuccess()) {
                // 调用正常
                return promise.getNow();
            } else {
                // 调用失败
                throw new DaoException(promise.cause());
            }
        }
    }

}
