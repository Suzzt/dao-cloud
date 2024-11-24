package com.dao.cloud.center.core;

import com.dao.cloud.center.web.vo.ConfigurationFileVO;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: sucf
 * @date: 2024/11/24 20:30
 * @description:
 */
@Slf4j
public class ConfigurationCenterManager {
    /**
     * 保存配置文件
     *
     * @param version  文件修改版本号
     * @param property 配置信息
     */
    public void save(String version, String property) {

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
}
