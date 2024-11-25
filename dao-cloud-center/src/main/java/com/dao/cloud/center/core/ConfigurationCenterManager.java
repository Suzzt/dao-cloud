package com.dao.cloud.center.core;

import com.dao.cloud.center.web.vo.ConfigurationFileVO;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import com.dao.cloud.core.exception.DaoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author: sucf
 * @date: 2024/11/24 20:30
 * @description:
 */
@Slf4j
public class ConfigurationCenterManager {

    private ConfigurationFileStrategy configurationFileStrategy;

    /**
     * 保存配置文件
     *
     * @param fileType 文件类型
     * @param version  文件修改版本号
     * @param property 配置信息
     */
    public void save(int fileType, String version, String property) {
    }

    /**
     * 获取文件列表
     *
     * @param proxy
     * @param groupId
     * @return
     */
    public List<ConfigurationFileVO> getConfigurationFile(String proxy, String groupId) {
        return null;
    }

    /**
     * 获取配置信息
     *
     * @param proxy
     * @param groupId
     * @param fileName
     * @return
     */
    public ConfigurationVO getConfiguration(String proxy, String groupId, String fileName) {
        return null;
    }

    public interface ConfigurationFileStrategy {
        /**
         * 保存文件内容
         *
         * @param filePath 文件路径
         * @param content  文件内容（字符串格式）
         * @throws IOException
         */
        void save(String filePath, String content) throws IOException;

        /**
         * 合并文件内容
         *
         * @param existingData 原始数据（Map格式）
         * @param newData      新数据（Map格式）
         * @return 合并后的数据；如果冲突无法合并，则返回抛异常
         */
        Map<String, Object> merge(Map<String, Object> existingData, Map<String, Object> newData);
    }

    public class PropertiesConfigurationFileStrategy implements ConfigurationFileStrategy {

        @Override
        public void save(String filePath, String content) throws IOException {
            File file = new File(filePath);
            Properties newProperties = loadPropertiesFromString(content);

            if (!file.exists()) {
                saveToFile(file, newProperties);
                return;
            }

            // 读取现有数据
            Properties existingProperties = loadPropertiesFromFile(file);

            // 合并数据
            Properties mergedProperties = mergeData(existingProperties, newProperties);

            if (mergedProperties == null) {
                throw new DaoException("There are conflicts in the file that cannot be resolved, please exit the merge to resolve the conflict before saving");
            } else {
                saveToFile(file, mergedProperties);
            }
        }

        @Override
        public Map<String, Object> merge(Map<String, Object> existingData, Map<String, Object> newData) {
            for (String key : newData.keySet()) {
                if (existingData.containsKey(key) && !existingData.get(key).equals(newData.get(key))) {
                    return null;
                }
            }
            existingData.putAll(newData);
            return existingData;
        }

        private Properties mergeData(Properties existingProperties, Properties newProperties) {
            for (String key : newProperties.stringPropertyNames()) {
                if (existingProperties.containsKey(key) && !existingProperties.getProperty(key).equals(newProperties.getProperty(key))) {
                    return null;
                }
            }
            existingProperties.putAll(newProperties);
            return existingProperties;
        }

        private Properties loadPropertiesFromString(String content) throws IOException {
            Properties properties = new Properties();
            properties.load(new StringReader(content));
            return properties;
        }

        private Properties loadPropertiesFromFile(File file) throws IOException {
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            }
            return properties;
        }

        private void saveToFile(File file, Properties properties) throws IOException {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                properties.store(outputStream, null);
            }
        }
    }

    class YamlConfigurationFileStrategy implements ConfigurationFileStrategy {

        private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        @Override
        public void save(String filePath, String content) throws IOException {
            File file = new File(filePath);

            // 将新内容解析为 Map
            Map<String, Object> newData = yamlMapper.readValue(content, Map.class);

            if (!file.exists()) {
                // 文件不存在，直接保存
                saveToFile(file, newData);
                return;
            }

            // 读取现有数据
            Map<String, Object> existingData = yamlMapper.readValue(file, Map.class);

            // 合并数据
            Map<String, Object> mergedData = merge(existingData, newData);

            if (mergedData == null) {
                throw new DaoException("There are conflicts in the file that cannot be resolved, please exit the merge to resolve the conflict before saving");
            } else {
                saveToFile(file, mergedData);
            }
        }

        @Override
        public Map<String, Object> merge(Map<String, Object> existingData, Map<String, Object> newData) {
            for (String key : newData.keySet()) {
                if (existingData.containsKey(key)) {
                    Object existingValue = existingData.get(key);
                    Object newValue = newData.get(key);

                    if (existingValue instanceof Map && newValue instanceof Map) {
                        // 递归合并 Map
                        Map<String, Object> mergedSubMap = merge((Map<String, Object>) existingValue, (Map<String, Object>) newValue);
                        if (mergedSubMap == null) {
                            return null; // 子级冲突无法合并
                        }
                        existingData.put(key, mergedSubMap);
                    } else if (!existingValue.equals(newValue)) {
                        return null;
                    }
                } else {
                    // 新增键
                    existingData.put(key, newData.get(key));
                }
            }
            return existingData;
        }

        private void saveToFile(File file, Map<String, Object> data) throws IOException {
            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            yaml.dump(data, new FileWriter(file));
        }
    }
}
