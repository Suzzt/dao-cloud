package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.starter.manager.CenterChannelManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author: sucf
 * @date: 2023/2/12 16:37
 * @description: config center startup(temp)
 */
public class ConfigCenterBootstrap implements ApplicationListener<ApplicationEvent> {
    @Autowired
    private ConfigurableEnvironment environment;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        String remotePropertyConfig = getRemotePropertyConfig();
        loadRemotePropertyConfig(remotePropertyConfig);
    }

    /**
     * Load the configuration file into the Spring container
     *
     * @param remotePropertyConfig
     */
    private void loadRemotePropertyConfig(String remotePropertyConfig) {

    }

    /**
     * Obtain the configuration information from Center
     *
     * @return
     */
    private String getRemotePropertyConfig() {
        Channel channel = CenterChannelManager.getChannel();
        // todo
        return null;
    }
}
