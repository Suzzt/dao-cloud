package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
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

    /**
     * Save configuration file.
     *
     * @param fileName the name of the configuration file
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param property the content to save
     */
    public void save(String fileName, String proxy, String groupId, String property) {
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
     * Get configuration information.
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the name of the configuration file
     * @return the configuration VO
     */
    public ConfigurationVO getConfigurationVO(String proxy, String groupId, String fileName) {
        ConfigurationVO configurationVO = new ConfigurationVO();
        configurationVO.setProperty(getConfiguration(proxy, groupId, fileName));
        return configurationVO;
    }

    /**
     * Get configuration information.
     *
     * @param proxy    the proxy identifier
     * @param groupId  the group identifier
     * @param fileName the name of the configuration file
     * @return configuration content
     */
    public String getConfiguration(String proxy, String groupId, String fileName) {
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
}
