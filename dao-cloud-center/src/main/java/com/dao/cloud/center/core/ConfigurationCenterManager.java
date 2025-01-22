package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration Center Manager for handling configuration files.
 * <p>
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
        FileUtil.writeUtf8String(property, proxy + File.separator + groupId + File.separator + fileName);
    }

    /**
     * Get the list of configuration files.
     *
     * @param proxy   the proxy identifier
     * @param groupId the group identifier
     * @return a set of file names
     */
    public Set<String> getConfigurationFile(String proxy, String groupId) {
        String directoryPath = proxy + File.separator + groupId;
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            log.warn("Directory does not exist or is not a directory: {}", directoryPath);
            return new HashSet<>();
        }

        return FileUtil.loopFiles(directory).stream()
                .filter(File::isFile)
                .map(File::getName)
                .collect(Collectors.toSet());
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
        String filePath = proxy + File.separator + groupId + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            log.warn("Configuration file does not exist or is not a file: {}", filePath);
            return false;
        }

        try {
            boolean deleted = FileUtil.del(file);
            if (deleted) {
                log.info("Successfully deleted configuration file: {}", filePath);
            } else {
                log.error("Failed to delete configuration file: {}", filePath);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting configuration file: {}", filePath, e);
            return false;
        }
    }
}
