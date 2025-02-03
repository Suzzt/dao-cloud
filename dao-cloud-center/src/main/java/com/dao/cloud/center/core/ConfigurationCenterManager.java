package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.model.ConfigurationProperty;
import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Set;

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
        return persistence.getConfigurationFile(proxy, groupId);
    }

    /**
     * get configuration information list.
     *
     * @param proxy   the proxy identifier
     * @param groupId the group identifier
     * @param start   page index
     * @param length  page size
     * @return
     */
    public List<ConfigurationVO> getConfiguration(String proxy, String groupId, int start, int length) {
        // todo 文件存储：proxy 是一层文件夹，下一层groupId 是一层文件夹，然后下一层就是文件名是配置文件名，请你扫描出所有的配置文件名，上面的入参是proxy和groupId，start和length是分页参数
        return null;
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
        String filePath = proxy + File.separator + groupId + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            log.warn("Configuration file does not exist: {}", filePath);
            return null;
        }

        try {
            return FileUtil.readUtf8String(file);
        } catch (Exception e) {
            log.error("Error reading configuration file: {}", filePath, e);
            return null;
        }
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
