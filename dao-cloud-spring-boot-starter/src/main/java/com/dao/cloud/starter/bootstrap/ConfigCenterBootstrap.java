package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
import com.dao.cloud.starter.properties.DaoCloudServerProperties;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

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
        List<String> fileInformation = getRemoteFileInformation("1278268928239764234", 1);
    }


    /**
     * Obtain the configuration information from Center
     *
     * @return
     */
    private String getRemotePropertyConfig() {


        // get config property

        return null;
    }


    /**
     * Get the configuration file information from Center
     *
     * @param groupId
     * @param version
     * @return file information
     */
    private List<String> getRemoteFileInformation(String groupId, int version) {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

        // get config file information
        ConfigurationFileInformationRequestModel configurationFileInformationRequestModel = new ConfigurationFileInformationRequestModel();
        configurationFileInformationRequestModel.setGroupId(groupId);
        configurationFileInformationRequestModel.setVersion(version);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationFileInformationRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< Failed to get profile information and send a message >>>>>>>>>", future.cause());
            }
        });
        return null;
    }
}
