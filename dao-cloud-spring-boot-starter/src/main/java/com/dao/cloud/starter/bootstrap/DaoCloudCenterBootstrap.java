package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.starter.bootstrap.manager.CenterChannelManager;
import com.dao.cloud.starter.properties.DaoCloudCenterProperties;
import com.dao.cloud.core.exception.DaoException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: sucf
 * @date: 2023/6/10 12:39
 * @description:
 */
@Component
public class DaoCloudCenterBootstrap {
    @PostConstruct
    public void init() {
        try {
            // init the connection cluster
            CenterChannelManager.init(DaoCloudCenterProperties.ip);
        } catch (InterruptedException e) {
            throw new DaoException(e);
        }
    }
}
