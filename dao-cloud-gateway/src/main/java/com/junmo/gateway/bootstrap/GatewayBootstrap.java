package com.junmo.gateway.bootstrap;

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
        load();
        // registry center
        registry();
    }

    /**
     * start
     */
    public void load() {

    }

    public void registry() {

    }


}
