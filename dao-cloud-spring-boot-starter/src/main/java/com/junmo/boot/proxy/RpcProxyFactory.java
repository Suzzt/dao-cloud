package com.junmo.boot.proxy;

import cn.hutool.core.util.IdUtil;
import com.junmo.core.enums.Constant;
import com.junmo.boot.handler.RpcResponseMessageHandler;
import com.junmo.core.exception.DaoException;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.boot.channel.ClientChannelFactory;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2022/10/28 22:30
 * @description: 代理工厂
 */
@Slf4j
public class RpcProxyFactory {

    /**
     * 构建代理对象
     *
     * @param serviceClass 目标类
     * @param <T>          返回结果
     * @return
     */
    public static <T> T build(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ProxyHandler(serviceClass));
    }

    static class ProxyHandler implements InvocationHandler {
        /**
         * 需要被代理的对象
         */
        private Class<?> serviceClass;

        public ProxyHandler(Class<?> serviceClass) {
            this.serviceClass = serviceClass;
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
            DaoMessage message = new DaoMessage(Constant.MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, requestModel);
            // push message
            ClientChannelFactory.getChannel("127.0.0.1", 6661).writeAndFlush(message);

            // 异步！ promise 对象来处理异步接收的结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(ClientChannelFactory.getChannel("127.0.0.1", 6661).eventLoop());
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
