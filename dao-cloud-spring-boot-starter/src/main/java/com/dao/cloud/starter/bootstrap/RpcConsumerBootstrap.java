package com.dao.cloud.starter.bootstrap;

import com.google.common.collect.Sets;
import com.dao.cloud.starter.annotation.DaoReference;
import com.dao.cloud.starter.banlance.LoadBalance;
import com.dao.cloud.starter.bootstrap.manager.ClientManager;
import com.dao.cloud.starter.bootstrap.manager.RegistryManager;
import com.dao.cloud.starter.bootstrap.thread.SyncProviderServerTimer;
import com.dao.cloud.starter.bootstrap.unit.RpcProxy;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description: rpc consumer startup
 */
@Slf4j
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
                            ClientManager.add(proxyProviderModel, proxyProviders);
                        }
                        relyProxy.add(proxyProviderModel);
                        LoadBalance loadBalance = daoReference.loadBalance();
                        long timeout = daoReference.timeout();
                        Byte serialized = SerializeStrategyFactory.getSerializeType(daoReference.serializable().getName());
                        // get proxyObj
                        serviceProxy = RpcProxy.build(iface, proxyProviderModel, serialized, loadBalance.getDaoLoadBalance(), timeout);
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< pull proxy = {}, provider = {} server node error >>>>>>>>>>>", new ProviderModel(provider, version), e);
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
}
