package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.web.vo.ConfigurationVO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Set;

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
     * @param fileName
     * @param proxy
     * @param groupId
     * @param property
     */
    public void save(String fileName, String proxy, String groupId, String property) {
        FileUtil.writeUtf8String(property, proxy + File.separator + groupId + File.separator + fileName);
    }

    /**
     * 获取文件列表
     *
     * @param proxy
     * @param groupId
     * @return
     */
    public Set<String> getConfigurationFile(String proxy, String groupId) {

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
