package com.dao.cloud.center.core;

import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.model.LogMeta;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.LogModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author sucf
 * @since 1.0
 * log manager
 */
@Slf4j
public class LogManager implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                scanLogFiles();
            } catch (Exception e) {
                log.error("handler scan log files error", e);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Long EXPIRATION_TIME = 1000 * 60 * 60 * 3L;

    /**
     * 存储日志的路径
     */
    @Value(value = "${dao-cloud.center.log.path:/data/dao-cloud/logs}")
    private String logPath;

    /**
     * 介绍下这个缓存的结构
     * key: traceId
     * value: sort List
     */
    private final Cache<String, List<LogMeta>> logsMeta = CacheBuilder.newBuilder().initialCapacity(50).expireAfterWrite(2, TimeUnit.HOURS).removalListener(new RemovalListener<String, List<LogMeta>>() {
        @Override
        public void onRemoval(RemovalNotification<String, List<LogMeta>> notification) {
            if (notification.wasEvicted()) {
                clear(notification.getKey());
            }
        }
    }).build();

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
            String storagePath = logPath + File.separator + logModel.getTraceId() + File.separator + logMeta.getHappenTime() + File.separator + "message.log";

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

    /**
     * 清除日志
     *
     * @param traceId
     */
    public void clear(String traceId) {
        FileUtil.del(logPath + File.separator + traceId);
    }

    /**
     * 扫描日志文件(删除)
     */
    private void scanLogFiles() {
        File logDir = new File(logPath);
        if (logDir.exists() && logDir.isDirectory()) {
            File[] files = logDir.listFiles();
            if (files != null) {
                long twoSecondsAgo = System.currentTimeMillis() - EXPIRATION_TIME;
                for (File file : files) {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        long creationTime = attr.creationTime().toMillis();
                        if (creationTime < twoSecondsAgo) {
                            FileUtil.del(file);
                        }
                    } catch (Exception e) {
                        log.error("delete log (path={}) file error", file.toPath(), e);
                    }
                }
            }
        } else {
            log.error("Log directory does not exist or is not a directory. path={}", logPath);
            throw new DaoException(CodeEnum.COLLECTION_LOG_NOT_EXIST);
        }
    }
}
