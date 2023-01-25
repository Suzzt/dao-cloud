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
     * build rpc proxy
     *
     * @param serviceClass
     * @param proxy
     * @param daoLoadBalance
     * @param <T>
     * @return
     */
    public static <T> T build(Class<T> serviceClass, String proxy, DaoLoadBalance daoLoadBalance) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ProxyHandler(serviceClass, proxy, daoLoadBalance));
    }

    static class ProxyHandler implements InvocationHandler {

        private Class<?> serviceClass;

        private String proxy;

        private DaoLoadBalance daoLoadBalance;

        public ProxyHandler(Class<?> serviceClass, String proxy, DaoLoadBalance daoLoadBalance) {
            this.serviceClass = serviceClass;
            this.proxy = proxy;
            this.daoLoadBalance = daoLoadBalance;
        }

        @Override
        public Object invoke(Object obj, Method method, Object[] args) throws Exception {

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
            channel.writeAndFlush(message);

            // 异步！ promise 对象来处理异步接收的结果线程
            // todo 这里有个问题：万一发送链路上发送失败了 promise就一直等着阻塞了，当然改成sync()就没这个问题了  但是会牺牲这个异步调用的性能！
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
