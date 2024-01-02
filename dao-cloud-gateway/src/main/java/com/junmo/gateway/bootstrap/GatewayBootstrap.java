package com.junmo.gateway.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author: sucf
 * @date: 2024/1/2 17:31
 * @description:
 */
@Slf4j
public class GatewayBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * start
     */
    public void start() {
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    }
}
