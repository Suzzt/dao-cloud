package com.junmo.boot.registry;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.junmo.boot.annotation.DaoReference;
import com.junmo.boot.channel.ChannelClient;
import com.junmo.boot.proxy.RpcProxyFactory;
import com.junmo.common.util.ThreadPoolFactory;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ServerNodeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description:
 */
@Slf4j
@Component
public class ClientManager implements SmartInstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean, BeanFactoryAware {
    private final Map<String, Set<ChannelClient>> channelClientMap = Maps.newConcurrentMap();

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
                    List<ServerNodeModel> serverNodeModels = RegistryManager.poll(proxy);
                    if (CollectionUtils.isEmpty(serverNodeModels)) {
                        throw new DaoException("proxy = " + proxy + " not exist provider server");
                    }
                    Set<ChannelClient> channelClients = Sets.newLinkedHashSet();
                    for (ServerNodeModel serverNodeModel : serverNodeModels) {
                        channelClients.add(new ChannelClient(serverNodeModel.getIp(), serverNodeModel.getPort()));
                    }
                    channelClientMap.put(proxy, channelClients);
                    // get proxyObj
                    serviceProxy = RpcProxyFactory.build(iface, channelClients);
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
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

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
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("<<<<<<<<<<<thread interrupted...>>>>>>>>>>", e);
                }
                for (Map.Entry<String, Set<ChannelClient>> entry : channelClientMap.entrySet()) {
                    String proxy = entry.getKey();
                    Set<ChannelClient> oldChannelClients = entry.getValue();
                    Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                    List<ServerNodeModel> serverNodeModels;
                    try {
                        serverNodeModels = RegistryManager.poll(proxy);
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                ChannelClient channelClient = new ChannelClient(serverNodeModel.getIp(), serverNodeModel.getPort());
                                pollChannelClients.add(channelClient);
                            }
                        }
                        // new up server node
                        Set<ChannelClient> newUpChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(pollChannelClients, oldChannelClients);
                        oldChannelClients.addAll(newUpChannelClients);
                        // down server node
                        Set<ChannelClient> downChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(oldChannelClients, pollChannelClients);
                        oldChannelClients.remove(downChannelClients);
                        entry.setValue(oldChannelClients);
                        log.info(">>>>>>>>>>>proxy = {} poll server node success<<<<<<<<<<", proxy);
                    } catch (InterruptedException e) {
                        log.error("<<<<<<<<<<<poll server node fair>>>>>>>>>>>", e);
                        throw new DaoException(e);
                    }
                }
            }
        });
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(pollServerNodeThread);
    }
}
