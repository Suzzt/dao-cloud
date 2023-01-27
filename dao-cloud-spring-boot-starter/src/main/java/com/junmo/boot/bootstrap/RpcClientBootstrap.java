package com.junmo.boot.bootstrap;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.annotation.DaoReference;
import com.junmo.boot.banlance.LoadBalance;
import com.junmo.boot.proxy.RpcProxyFactory;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description: rpc client startup
 */
@Slf4j
@Component
public class RpcClientBootstrap implements SmartInstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean {

    private final Set<String> proxySet = new HashSet<>();

    private Thread pollServerNodeThread;

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
                Object serviceProxy;
                try {
                    // poll service node
                    List<ServerNodeModel> serverNodeModels = com.junmo.boot.bootstrap.RegistryManager.poll(proxy);
                    Set<ChannelClient> channelClients = Sets.newLinkedHashSet();
                    for (ServerNodeModel serverNodeModel : serverNodeModels) {
                        channelClients.add(new ChannelClient(proxy, serverNodeModel.getIp(), serverNodeModel.getPort()));
                    }
                    ClientManager.addAll(proxy, channelClients);
                    proxySet.add(proxy);
                    LoadBalance loadBalance = daoReference.loadBalance();
                    long timeout = daoReference.timeout();
                    // get proxyObj
                    serviceProxy = RpcProxyFactory.build(iface, proxy, loadBalance.getDaoLoadBalance(), timeout);
                } catch (InterruptedException e) {
                    log.error("<<<<<<<<<<<poll server node fair>>>>>>>>>>>", e);
                    throw new DaoException(e);
                }
                // set bean
                field.setAccessible(true);
                field.set(bean, serviceProxy);
                log.info(">>>>>>>>>>>dao-cloud, invoker init reference bean success<<<<<<<<<<< proxy = {}, beanName = {}");
            }
        });
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (pollServerNodeThread != null && pollServerNodeThread.isAlive()) {
            pollServerNodeThread.interrupt();
        }
        log.debug(">>>>>>>>>>> dao-cloud-rpc provider server destroy <<<<<<<<<<<<");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pollServerNodeThread = new Thread(() -> {
            while (true) {
                // 这里只是兜底方案,目的是整顿这个集群
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("<<<<<<<<<<<thread interrupted...>>>>>>>>>>", e);
                }
                for (String proxy : proxySet) {
                    Set<ChannelClient> oldChannelClients = ClientManager.getClients(proxy);
                    Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                    List<ServerNodeModel> serverNodeModels;
                    try {
                        serverNodeModels = com.junmo.boot.bootstrap.RegistryManager.poll(proxy);
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                ChannelClient channelClient = new ChannelClient(proxy, serverNodeModel.getIp(), serverNodeModel.getPort());
                                pollChannelClients.add(channelClient);
                            }
                        }
                        // new up server node
                        Set<ChannelClient> newUpChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(pollChannelClients, oldChannelClients);
                        ClientManager.addAll(proxy, newUpChannelClients);
                        // down server node
                        Set<ChannelClient> downChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(oldChannelClients, pollChannelClients);
                        ClientManager.removeAll(proxy, downChannelClients);
                        log.info(">>>>>>>>>>> proxy = {} poll server node success <<<<<<<<<<", proxy);
                    } catch (InterruptedException e) {
                        log.error("<<<<<<<<<<< poll server node fair >>>>>>>>>>>", e);
                        throw new DaoException(e);
                    }
                }
            }
        });
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(pollServerNodeThread);
    }
}
