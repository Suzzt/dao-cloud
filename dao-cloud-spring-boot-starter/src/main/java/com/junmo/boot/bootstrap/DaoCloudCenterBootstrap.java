package com.junmo.boot.bootstrap;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import com.junmo.boot.properties.DaoCloudCenterProperties;
import com.junmo.core.exception.DaoException;
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
        // init the connection cluster
        try {
            CenterChannelManager.init(DaoCloudCenterProperties.ip);
        } catch (InterruptedException e) {
            throw new DaoException(e);
        }
    }
}
