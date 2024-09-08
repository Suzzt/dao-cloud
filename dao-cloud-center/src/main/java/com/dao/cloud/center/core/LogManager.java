package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.model.LogMeta;
import com.dao.cloud.core.model.LogModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
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
     * 存储日志的路径
     */
    @Value(value = "${dao-cloud.center.log.path:/data/dao-cloud/logs}")
    private String logPath;

    /**
     * 介绍下这个缓存的结构
     * key: traceId
     * value: sort List
     * stage(1-1-2)+日志发生节点信息(ip、port....)
     */
    private final Cache<String, List<LogMeta>> cache = CacheBuilder.newBuilder()
            .initialCapacity(50)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    /**
     * 保存日志
     *
     * @param logModel
     */
    public synchronized void collect(LogModel logModel) {
        try {
            List<LogMeta> logMetas = cache.get(logModel.getTraceId(), Lists::newArrayList);
            LogMeta logMeta = new LogMeta();
            logMeta.setNode(logModel.getNode());
            logMeta.setStage(logModel.getStage());
            logMetas.add(logMeta);

            // path
            String storagePath = logPath + "/" + logModel.getTraceId() + File.separator + "message.log";

            FileUtil.appendUtf8String(logModel.getLogMessage() + "\n", storagePath);
            cache.put(logModel.getTraceId(), logMetas);
        } catch (ExecutionException e) {
            log.info("dao cloud log collect error", e);
        }
    }

    public static List<LogModel> get(String traceId) {
        return null;
    }
}
