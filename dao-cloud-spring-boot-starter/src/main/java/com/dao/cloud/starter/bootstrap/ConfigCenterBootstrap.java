package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import com.dao.cloud.core.model.ConfigurationPropertyRequestModel;
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
    }

    /**
     * Load the configuration file into the Spring container
     *
     * @param remotePropertyConfig
     */
    private void loadRemotePropertyConfig(String remotePropertyConfig) {
        String groupId = "";
        int version = 0;
        List<String> fileInformationList = getRemoteFileInformation(groupId, version);
        for (String fileInformation : fileInformationList) {
            String content = getRemotePropertyConfig(groupId, version, fileInformation);
        }
    }


    /**
     * Obtain the configuration information from Center
     *
     * @return
     */
    private String getRemotePropertyConfig(String groupId, int version, String fileName) {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

        ConfigurationPropertyRequestModel configurationPropertyRequestModel = new ConfigurationPropertyRequestModel();
        configurationPropertyRequestModel.setGroupId(groupId);
        configurationPropertyRequestModel.setVersion(version);
        configurationPropertyRequestModel.setFileName(fileName);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_PROPERTY_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationPropertyRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< Failed to send a request to pull the center remote configuration >>>>>>>>>", future.cause());
            }
        });
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

        ConfigurationFileInformationRequestModel configurationFileInformationRequestModel = new ConfigurationFileInformationRequestModel();
        configurationFileInformationRequestModel.setGroupId(groupId);
        configurationFileInformationRequestModel.setVersion(version);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationFileInformationRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<Failed to send a request to pull the center remote file information >>>>>>>>>", future.cause());
            }
        });
        return null;
    }
}
