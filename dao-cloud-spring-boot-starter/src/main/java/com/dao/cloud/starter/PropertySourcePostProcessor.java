package com.dao.cloud.starter;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import com.dao.cloud.core.model.ConfigurationPropertyRequestModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.LongPromiseBuffer;
import com.dao.cloud.starter.manager.CenterChannelManager;
import com.dao.cloud.starter.properties.DaoCloudPropertySourceProperties;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Remote profiles are automatically configured
 *
 * @author sucf
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class PropertySourcePostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            MutablePropertySources propertySources = environment.getPropertySources();
            loadRemotePropertyConfig(propertySources, environment);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    /**
     * Load the configuration file into the Spring container
     */
    private void loadRemotePropertyConfig(MutablePropertySources propertySources, ConfigurableEnvironment environment) throws Exception {
        String proxy = environment.getProperty("dao-cloud.configuration.proxy");
        String groupId = environment.getProperty("dao-cloud.configuration.groupId");
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(groupId)) {
            return;
        }
        Binder binder = Binder.get(environment);
        DaoCloudPropertySourceProperties propertySourceProperties = binder.bind("dao-cloud.configuration",
                        Bindable.of(DaoCloudPropertySourceProperties.class))
                .orElse(new DaoCloudPropertySourceProperties());

        Set<String> fileNameSet = getRemoteFileInformation(proxy, groupId);
        for (String fileName : fileNameSet) {
            PropertySource<?> propertySource = loadRemotePropertySource(proxy, groupId, fileName);
            if (propertySource == null) {
                continue;
            }
            if (!propertySourceProperties.isAllowOverride() || (!propertySourceProperties.isOverrideNone() && propertySourceProperties.isOverrideSystemProperties())) {
                propertySources.addFirst(propertySource);
                continue;
            }
            propertySources.addLast(propertySource);
        }
    }

    private PropertySource<?> loadRemotePropertySource(String proxy, String groupId, String fileName) throws Exception {
        String fileContent = getRemotePropertyConfig(proxy, groupId, fileName);
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            List<PropertySource<?>> sources = loader.load(fileName, new ByteArrayResource(fileContent.getBytes()));
            return sources.get(0);
        } else if (fileName.endsWith(".properties")) {
            PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();
            List<PropertySource<?>> sources = loader.load(fileName, new ByteArrayResource(fileContent.getBytes()));
            return sources.get(0);
        } else {
            log.warn("Unsupported file type for remote configuration: {}", fileName);
            return null;
        }
    }

    /**
     * Obtain the configuration information from Center
     *
     * @param proxy    proxy
     * @param groupId  groupId
     * @param fileName fileName
     * @return configuration information
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
        configurationPropertyRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        LongPromiseBuffer.getInstance().put(configurationPropertyRequestModel.getSequenceId(), promise);
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.PULL_CENTER_CONFIGURATION_PROPERTY_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationPropertyRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< Failed to send a request to pull the center remote configuration >>>>>>>>>", future.cause());
            }
        });
        if (!promise.await(5 * 1_000)) {
            throw new DaoException("get remote configuration property wait time out");
        }
        if (promise.isSuccess()) {
            return (String) promise.getNow();
        } else {
            throw new InterruptedException("get remote configuration property error");
        }
    }


    /**
     * Get the configuration file information from Center
     *
     * @param proxy   proxy
     * @param groupId groupId
     * @return file information
     */
    private Set<String> getRemoteFileInformation(String proxy, String groupId) throws InterruptedException {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }

        ConfigurationFileInformationRequestModel configurationFileInformationRequestModel = new ConfigurationFileInformationRequestModel();
        configurationFileInformationRequestModel.setSequenceId(new Snowflake(2, 2).nextId());
        configurationFileInformationRequestModel.setGroupId(groupId);
        configurationFileInformationRequestModel.setProxy(proxy);
        configurationFileInformationRequestModel.setSequenceId(IdUtil.getSnowflake(2, 2).nextId());
        Promise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        LongPromiseBuffer.getInstance().put(configurationFileInformationRequestModel.getSequenceId(), promise);
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configurationFileInformationRequestModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<Failed to send a request to pull the center remote file information >>>>>>>>>", future.cause());
            }
        });
        if (!promise.await(5 * 1_000)) {
            throw new DaoException("get remote file information wait time out");
        }
        if (promise.isSuccess()) {
            return (Set<String>) promise.getNow();
        } else {
            throw (DaoException) promise.cause();
        }
    }
}
