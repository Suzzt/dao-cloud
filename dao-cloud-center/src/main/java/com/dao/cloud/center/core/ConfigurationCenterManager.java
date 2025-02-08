package com.dao.cloud.center.core;

import com.dao.cloud.center.core.model.ConfigurationModel;
import com.dao.cloud.center.core.model.ConfigurationProperty;
import com.dao.cloud.center.core.storage.Persistence;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration Center Manager for handling configuration files.
 * Author: sucf
 * Date: 2024/11/24 20:30
 */
@Slf4j
public class ConfigurationCenterManager {

    private final Persistence persistence;

    public ConfigurationCenterManager(Persistence persistence) {
        this.persistence = persistence;
    }

    /**
     * Save configuration file.
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the name of the configuration file
     * @param property the content to save
     */
    public void save(String proxy, String groupId, String fileName, String property) {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        configurationProperty.setProxy(proxy);
        configurationProperty.setGroupId(groupId);
        configurationProperty.setFileName(fileName);
        configurationProperty.setProperty(property);
        persistence.storage(configurationProperty);
    }

    /**
     * 获取配置文件名列表
     *
     * @param proxy   the proxy identifier
     * @param groupId the group identifier
     * @return 配置文件文件名列表
     */
    public Set<String> getConfigurationFile(String proxy, String groupId) {
        Set<String> result = Sets.newHashSet();
        List<ConfigurationModel> configurationModelList = persistence.getConfiguration();
        for (ConfigurationModel configurationModel : configurationModelList) {
            if (configurationModel.getProxy().equals(proxy) && configurationModel.getGroupId().equals(groupId)) {
                result.add(configurationModel.getFileName());
            }
        }
        return result;
    }

    /**
     * get configuration information list(like pagination).
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param pageNo   page index
     * @param pageSize page size
     * @return ConfigurationModel
     */
    public List<ConfigurationModel> getConfiguration(String proxy, String groupId, int pageNo, int pageSize) {
        List<ConfigurationModel> configurationModelList = persistence.getConfiguration();

        List<ConfigurationModel> filteredList = configurationModelList.stream()
                .filter(config -> (proxy == null || config.getProxy().contains(proxy)) &&
                        (groupId == null || config.getGroupId().contains(groupId)))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filteredList)) {
            return null;
        }
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredList.size());
        return filteredList.subList(fromIndex, toIndex);
    }


    /**
     * Get configuration information.
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the name of the configuration file
     * @return configuration content
     */
    public String getConfigurationProperty(String proxy, String groupId, String fileName) {
        return persistence.getConfigurationProperty(proxy, groupId, fileName);
    }

    /**
     * Delete a configuration file.
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the name of the configuration file
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean delete(String proxy, String groupId, String fileName) {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        configurationProperty.setProxy(proxy);
        configurationProperty.setGroupId(groupId);
        configurationProperty.setFileName(fileName);
        persistence.delete(configurationProperty);
        return true;
    }
}
