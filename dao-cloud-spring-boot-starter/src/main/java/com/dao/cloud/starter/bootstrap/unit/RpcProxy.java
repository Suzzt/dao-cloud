package com.dao.cloud.starter.bootstrap.unit;

import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RpcRequestModel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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

            RpcRequestModel requestModel = new RpcRequestModel(
                    providerModel.getProvider(),
                    providerModel.getVersion(),
                    method.getName(),
                    method.getParameterTypes(),
                    args,
                    method.getReturnType()
            );

            ClientInvoker clientInvoker = new ClientInvoker(proxyProviderModel, daoLoadBalance, serialized, timeout);
            return clientInvoker.invoke(requestModel);
        }
    }
}
