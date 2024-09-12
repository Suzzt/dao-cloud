package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.model.LogMeta;
import com.dao.cloud.core.model.LogModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
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
    private final Cache<String, List<LogMeta>> logsMeta = CacheBuilder.newBuilder().initialCapacity(50).expireAfterWrite(2, TimeUnit.HOURS).build();

    /**
     * 保存日志
     *
     * @param logModel
     */
    public void collect(LogModel logModel) {
        try {
            List<LogMeta> logMetas = logsMeta.get(logModel.getTraceId(), Lists::newArrayList);
            LogMeta logMeta = new LogMeta();
            logMeta.setNode(logModel.getNode());
            logMeta.setHappenTime(logModel.getHappenTime());
            logMetas.add(logMeta);

            // path
            String storagePath = logPath + "/" + logModel.getTraceId() + File.separator + logMeta.getHappenTime() + File.separator + "message.log";

            FileUtil.appendUtf8String(logModel.getLogMessage(), storagePath);
            logsMeta.put(logModel.getTraceId(), logMetas);
        } catch (ExecutionException e) {
            log.info("dao cloud log collect error", e);
        }
    }

    public List<LogModel> get(String traceId) throws ExecutionException {
        List<LogMeta> logMetas = logsMeta.get(traceId, new Callable<List<LogMeta>>() {
            @Override
            public List<LogMeta> call() throws Exception {
                return null;
            }
        });
        if (CollectionUtils.isEmpty(logMetas)) {
            return null;
        }
        List<LogModel> logModels = Lists.newArrayList();
        for (LogMeta logMeta : logMetas) {
            String storagePath = logPath + "/" + traceId + File.separator + logMeta.getHappenTime() + File.separator + "message.log";
            String logMessage = FileUtil.readUtf8String(storagePath);
            LogModel logModel = new LogModel();
            logModel.setTraceId(traceId);
            logModel.setLogMessage(logMessage);
            logModel.setNode(logMeta.getNode());
            logModels.add(logModel);
        }
        return logModels;
    }
}
