package com.dao.cloud.gateway;

import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.gateway.manager.GatewayPullServiceTimer;
import com.dao.cloud.starter.bootstrap.manager.RegistryManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2024/1/2 17:31
 * @description: gateway bootstrap
 */
@Slf4j
public class GatewayBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // load all service && start thread task pull service
        loadPull();
        // registry center
        registry();
    }

    /**
     * load and start a new pull service task
     */
    public void loadPull() {
        Thread timer = new Thread(new GatewayPullServiceTimer());
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(timer);
    }

    /**
     * registry gateway node to center
     */
    public void registry() {
        RegisterProviderModel gatewayNodeModel = new RegisterProviderModel();
        gatewayNodeModel.setProxy(DaoCloudConstant.GATEWAY_PROXY);
        Set<ProviderModel> providerModels = new HashSet<>();
        // version todo 这个后面做成可配置化的,这个是有用的,区分环境
        ProviderModel providerModel = new ProviderModel(DaoCloudConstant.GATEWAY, 0);
        providerModels.add(providerModel);
        gatewayNodeModel.setProviderModels(providerModels);
        gatewayNodeModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudConstant.GATEWAY_PORT));
        RegistryManager.registry(gatewayNodeModel);
    }
}
