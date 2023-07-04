package com.junmo.center.core.storage;

import com.junmo.center.bootstarp.DaoCloudClusterCenterProperties;
import com.junmo.core.expand.Persistence;
import org.springframework.util.StringUtils;

/**
 * @author: sucf
 * @date: 2023/3/1 15:08
 * @description: abstract template persistence
 */
public abstract class AbstractPersistence implements Persistence {
    public void init() {
        if (StringUtils.hasLength(DaoCloudClusterCenterProperties.ip)) {
            // from cluster sync config data

        } else {
            // stand-alone mode starts
            load();
        }
    }
}
