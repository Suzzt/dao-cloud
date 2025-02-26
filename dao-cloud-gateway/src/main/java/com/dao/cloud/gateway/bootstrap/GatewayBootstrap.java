package com.dao.cloud.gateway.bootstrap;

import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.gateway.properties.DaoCloudGatewayProperties;
import com.dao.cloud.gateway.timer.GatewayPullServiceTimer;
import com.dao.cloud.starter.manager.RegistryManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sucf
 * @since 1.0
 * gateway bootstrap
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
        int version = DaoCloudGatewayProperties.version == null ? 0 : DaoCloudGatewayProperties.version;
        ProviderModel providerModel = new ProviderModel(DaoCloudConstant.GATEWAY, version);
        providerModels.add(providerModel);
        gatewayNodeModel.setProviderModels(providerModels);
        gatewayNodeModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudConstant.GATEWAY_PORT));
        RegistryManager.registry(gatewayNodeModel);
    }
}
