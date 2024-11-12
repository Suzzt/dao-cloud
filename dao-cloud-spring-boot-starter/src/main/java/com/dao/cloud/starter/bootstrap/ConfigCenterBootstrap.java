package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2023/2/12 16:37
 * @description: config center startup(temp)
 */
@Component
@Slf4j
public class ConfigCenterBootstrap implements ApplicationListener<ApplicationEvent> {

    private final ConfigurableEnvironment environment;

    public ConfigCenterBootstrap(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

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
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

//        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, registerProviderModel);
//        channel.writeAndFlush(daoMessage).addListener(future -> {
//            if (!future.isSuccess()) {
//                log.error("<<<<<<<<< send register server error >>>>>>>>>", future.cause());
//            }
//        });
        // todo
        return null;
    }
}
