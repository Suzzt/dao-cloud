package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RpcRequestModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.starter.annotation.DaoReference;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.banlance.LoadBalance;
import com.dao.cloud.starter.manager.ClientManager;
import com.dao.cloud.starter.manager.RegistryManager;
import com.dao.cloud.starter.timer.SyncProviderServerTimer;
import com.dao.cloud.starter.unit.ClientInvoker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description: rpc consumer startup
 */
@Slf4j
@Component
public class RpcConsumerBootstrap implements ApplicationListener<ContextRefreshedEvent>, SmartInstantiationAwareBeanPostProcessor, DisposableBean {

    private final Set<ProxyProviderModel> relyProxy = new HashSet<>();

    private Thread pullServerNodeThread;

    private final Set<Object> fields = new HashSet<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        delayedLoad();
        pullServerNodeThread = new Thread(new SyncProviderServerTimer(relyProxy));
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(pullServerNodeThread);
    }

    public void delayedLoad() {
        for (Object bean : fields) {
            ReflectionUtils.doWithFields(bean.getClass(), field -> {
                if (field.isAnnotationPresent(DaoReference.class)) {
                    // valid
                    Class iface = field.getType();
                    if (!iface.isInterface()) {
                        throw new DaoException("dao-cloud reference(DaoReference) must be interface.");
                    }
                    DaoReference daoReference = field.getAnnotation(DaoReference.class);
                    String proxy = daoReference.proxy();
                    String provider = StringUtils.hasLength(daoReference.provider()) ? daoReference.provider() : iface.getName();
                    int version = daoReference.version();
                    Object serviceProxy;
                    try {
                        // pull service node
                        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
                        Set<ServerNodeModel> serverNodeModels = RegistryManager.pull(proxyProviderModel);
                        Set<ServerNodeModel> proxyProviders = Sets.newLinkedHashSet();
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                proxyProviders.add(serverNodeModel);
                            }
                            ClientManager.save(proxyProviderModel, proxyProviders);
                        }
                        relyProxy.add(proxyProviderModel);
                        LoadBalance loadBalance = daoReference.loadBalance();
                        long timeout = daoReference.timeout();
                        Byte serialized = SerializeStrategyFactory.getSerializeType(daoReference.serializable().getName());
                        // get proxyObj
                        serviceProxy = RpcProxy.build(iface, proxyProviderModel, serialized, loadBalance.getDaoLoadBalance(), timeout);
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< pull proxy provider = {} server node error >>>>>>>>>>>", new ProviderModel(provider, version), e);
                        throw new DaoException(e);
                    }
                    // set bean
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);
                    log.info(">>>>>>>>>>> dao-cloud, invoker init reference bean success <<<<<<<<<<< proxy = {}, beanName = {}", proxy, field.getName());
                }
            });
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(DaoReference.class)) {
                fields.add(bean);
            }
        });
        return true;
    }

    @Override
    public void destroy() {
        if (pullServerNodeThread != null && pullServerNodeThread.isAlive()) {
            pullServerNodeThread.interrupt();
        }
        log.debug(">>>>>>>>>>> dao-cloud-rpc consumer server destroy <<<<<<<<<<<<");
    }

    private static class RpcProxy {

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
            return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new RpcProxy.ProxyHandler(proxyProviderModel, serialized, daoLoadBalance, timeout));
        }

        /**
         * proxy rpc handler
         */
        private static class ProxyHandler implements InvocationHandler {

            private final ProxyProviderModel proxyProviderModel;

            private final byte serialized;

            private final DaoLoadBalance daoLoadBalance;

            private final long timeout;

            public ProxyHandler(ProxyProviderModel proxyProviderModel, byte serialized, DaoLoadBalance daoLoadBalance, long timeout) {
                this.proxyProviderModel = proxyProviderModel;
                this.serialized = serialized;
                this.daoLoadBalance = daoLoadBalance;
                this.timeout = timeout;
            }

            @Override
            public Object invoke(Object obj, Method method, Object[] args) throws InterruptedException {
                ProviderModel providerModel = proxyProviderModel.getProviderModel();

                RpcRequestModel requestModel = new RpcRequestModel(providerModel.getProvider(), providerModel.getVersion(), method.getName(), method.getParameterTypes(), args, method.getReturnType());

                ClientInvoker clientInvoker = new ClientInvoker(proxyProviderModel, daoLoadBalance, serialized, timeout);
                return clientInvoker.invoke(requestModel);
            }
        }
    }
}
