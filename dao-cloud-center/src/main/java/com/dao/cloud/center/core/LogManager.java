package com.dao.cloud.center.core;

import com.dao.cloud.core.model.LogModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2024/8/23 17:02
 * @description: log manager
 */
@Slf4j
public class LogManager {
    /**
     * 介绍下这个缓存的结构
     * key: traceId
     * value: sort List
     * stage(1-1-2)+日志发生节点信息(ip、port....)
     */
    private static final Cache<String, List<LogModel>> cache;

    static {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(50)
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build();
    }

    /**
     * 保存日志
     *
     * @param logModel
     */
    public static synchronized void put(LogModel logModel) {
        try {
            List<LogModel> logModels = cache.get(logModel.getTraceId(), Lists::newArrayList);
            logModels.add(logModel);
        } catch (ExecutionException e) {
            log.info("log save error", e);
        }
    }
}
