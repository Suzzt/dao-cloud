package com.junmo.gateway.bootstrap;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import com.junmo.boot.bootstrap.manager.RegistryManager;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.RegisterProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.NetUtil;
import com.junmo.core.util.ThreadPoolFactory;
import com.junmo.gateway.bootstrap.thread.GatewayPullServiceTimer;
import com.junmo.gateway.hanlder.PullServiceNodeMessageHandler;
import com.junmo.gateway.properties.GatewayProperties;
import lombok.extern.slf4j.Slf4j;
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

    private GatewayProperties gatewayProperties;

    public GatewayBootstrap(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // Newly added gateway handler
        CenterChannelManager.getChannel().pipeline().addLast(new PullServiceNodeMessageHandler());
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
        ProviderModel providerModel = new ProviderModel("gateway", 0);
        providerModels.add(providerModel);
        gatewayNodeModel.setProviderModels(providerModels);
        gatewayNodeModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudConstant.GATEWAY_PORT));
        RegistryManager.registry(gatewayNodeModel);
    }
}
