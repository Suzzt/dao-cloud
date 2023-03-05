package com.junmo.center.core.storage;

import com.junmo.center.bootstarp.DaoCloudConfigCenterProperties;

import javax.annotation.Resource;

/**
 * @author: sucf
 * @date: 2023/3/1 15:08
 * @description: abstract template persistence
 */
public abstract class AbstractPersistence implements Persistence {
    @Resource
    public DaoCloudConfigCenterProperties daoCloudConfigCenterProperties;
}
