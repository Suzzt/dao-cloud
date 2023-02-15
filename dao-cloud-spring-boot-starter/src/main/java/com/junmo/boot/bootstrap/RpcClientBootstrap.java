package com.junmo.boot.bootstrap;

import com.google.common.collect.Sets;
import com.junmo.boot.annotation.DaoReference;
import com.junmo.boot.banlance.LoadBalance;
import com.junmo.boot.bootstrap.proxy.RpcProxyFactory;
import com.junmo.boot.bootstrap.thread.SyncServerTimer;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.ThreadPoolFactory;
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

import java.util.HashSet;
import java.util.Set;


/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description: rpc client startup
 */
@Slf4j
@Component
public class RpcClientBootstrap implements ApplicationListener<ContextRefreshedEvent>, SmartInstantiationAwareBeanPostProcessor, DisposableBean {

    private final Set<ProxyProviderModel> relyProxy = new HashSet<>();

    private Thread pollServerNodeThread;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        pollServerNodeThread = new Thread(new SyncServerTimer(relyProxy));
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(pollServerNodeThread);
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(DaoReference.class)) {
                // valid
                Class iface = field.getType();
                if (!iface.isInterface()) {
                    throw new DaoException("dao-cloud reference(DaoReference) must be interface.");
                }
                DaoReference daoReference = field.getAnnotation(DaoReference.class);
                String proxy = daoReference.proxy();
                String provider = StringUtils.hasLength(daoReference.provider()) ? daoReference.provider() : iface.getSimpleName();
                int version = daoReference.version();
                Object serviceProxy;
                try {
                    // poll service node
                    ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
                    Set<ServerNodeModel> serverNodeModels = RegistryManager.poll(proxyProviderModel);
                    Set<ChannelClient> channelClients = Sets.newLinkedHashSet();
                    if (!CollectionUtils.isEmpty(serverNodeModels)) {
                        for (ServerNodeModel serverNodeModel : serverNodeModels) {
                            channelClients.add(new ChannelClient(proxyProviderModel, serverNodeModel.getIp(), serverNodeModel.getPort()));
                        }
                        ClientManager.addAll(proxyProviderModel, channelClients);
                    }
                    relyProxy.add(proxyProviderModel);
                    LoadBalance loadBalance = daoReference.loadBalance();
                    long timeout = daoReference.timeout();
                    // get proxyObj
                    serviceProxy = RpcProxyFactory.build(iface, proxyProviderModel, loadBalance.getDaoLoadBalance(), timeout);
                } catch (Exception e) {
                    log.error("<<<<<<<<<<< poll proxy = {}, provider = {} server node error >>>>>>>>>>>", new ProviderModel(provider, version), e);
                    throw new DaoException(e);
                }
                // set bean
                field.setAccessible(true);
                field.set(bean, serviceProxy);
                log.info(">>>>>>>>>>> dao-cloud, invoker init reference bean success <<<<<<<<<<< proxy = {}, beanName = {}", proxy, beanName);
            }
        });
        return true;
    }

    @Override
    public void destroy() {
        if (pollServerNodeThread != null && pollServerNodeThread.isAlive()) {
            pollServerNodeThread.interrupt();
        }
        log.debug(">>>>>>>>>>> dao-cloud-rpc consumer server destroy <<<<<<<<<<<<");
    }
}
