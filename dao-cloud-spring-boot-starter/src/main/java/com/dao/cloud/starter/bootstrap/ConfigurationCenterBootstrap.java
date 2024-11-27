package com.dao.cloud.starter.bootstrap;

import cn.hutool.core.util.IdUtil;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import com.dao.cloud.core.model.ConfigurationPropertyRequestModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
import com.dao.cloud.starter.properties.DaoCloudConfigProperties;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/12 16:37
 * @description: Configuration center startup
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DaoCloudConfigProperties.class)
public class ConfigurationCenterBootstrap implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        loadRemotePropertyConfig(propertySources);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * Load the configuration file into the Spring container
     */
    @SneakyThrows
    private void loadRemotePropertyConfig(MutablePropertySources propertySources) {
        String proxy = "";
        String groupId = "";
        DaoCloudConfigProperties daoCloudConfigProperties = new DaoCloudConfigProperties();
        Set<String> fileNameSet = getRemoteFileInformation(proxy, groupId);
        for (String fileName : fileNameSet) {
            String yamlContent = getRemotePropertyConfig(proxy, groupId, fileName);
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(yamlContent);

            MapPropertySource propertySource = new MapPropertySource(fileName, yamlMap);
            propertySources.addLast(propertySource);
        }
    }


    /**
     * Obtain the configuration information from Center
     *
     * @param proxy
     * @param groupId
     * @param fileName
     * @return
     * @throws InterruptedException
     */
    private String getRemotePropertyConfig(String proxy, String groupId, String fileName) throws InterruptedException {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

        ConfigurationPropertyRequestModel configurationPropertyRequestModel = new ConfigurationPropertyRequestModel();
        configurationPropertyRequestModel.setProxy(proxy);
        configurationPropertyRequestModel.setGroupId(groupId);
        configurationPropertyRequestModel.setFileName(fileName);
        DefaultPromise<String> promise = new DefaultPromise<>(channel.eventLoop());
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_PROPERTY_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationPropertyRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< Failed to send a request to pull the center remote configuration >>>>>>>>>", future.cause());
            }
        });
        if (!promise.await(5 * 1_000)) {
            throw new DaoException("get remote configuration property wait time out");
        }
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw (DaoException) promise.cause();
        }
    }


    /**
     * Get the configuration file information from Center
     *
     * @param proxy
     * @param groupId
     * @return file information
     */
    private Set<String> getRemoteFileInformation(String proxy, String groupId) throws InterruptedException {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

        ConfigurationFileInformationRequestModel configurationFileInformationRequestModel = new ConfigurationFileInformationRequestModel();
        configurationFileInformationRequestModel.setGroupId(groupId);
        configurationFileInformationRequestModel.setProxy(proxy);
        configurationFileInformationRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
        DefaultPromise<Set<String>> promise = new DefaultPromise<>(channel.eventLoop());
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationFileInformationRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<Failed to send a request to pull the center remote file information >>>>>>>>>", future.cause());
            }
        });
        if (!promise.await(5 * 1_000)) {
            throw new DaoException("get remote file information wait time out");
        }
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw (DaoException) promise.cause();
        }
    }
}
