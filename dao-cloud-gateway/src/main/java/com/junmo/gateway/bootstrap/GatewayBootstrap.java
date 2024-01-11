package com.junmo.gateway.bootstrap;

import com.junmo.boot.bootstrap.thread.SyncProviderServerTimer;
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

    public void registry() {

    }


}
