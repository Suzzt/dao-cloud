package com.junmo.gateway.bootstrap;

import com.junmo.boot.bootstrap.manager.RegistryManager;
import com.junmo.boot.properties.DaoCloudServerProperties;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.NetUtil;
import com.junmo.core.util.ThreadPoolFactory;
import com.junmo.gateway.bootstrap.thread.GatewayPullServiceTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author: sucf
 * @date: 2024/1/2 17:31
 * @description: gateway bootstrap
 */
@Slf4j
public class GatewayBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // load all service && start thread task pull service
        loadPull();
        // registry center
        registry();
    }

    /**
     * start
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
        gatewayNodeModel.setProxy("dao-cloud-gateway");
        gatewayNodeModel.setProviderModels(null);
        gatewayNodeModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudServerProperties.serverPort));
        RegistryManager.registry(gatewayNodeModel);
    }
}
