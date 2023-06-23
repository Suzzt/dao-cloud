package com.junmo.boot.bootstrap;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import com.junmo.boot.properties.DaoCloudCenterProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2023/6/10 12:39
 * @description:
 */
@Component
public class DaoCloudCenterBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // init the connection cluster
        try {
            CenterChannelManager.init(DaoCloudCenterProperties.ip);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
