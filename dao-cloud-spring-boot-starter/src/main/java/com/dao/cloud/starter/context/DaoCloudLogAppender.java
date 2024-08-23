package com.dao.cloud.starter.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.io.FileUtil;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author: sucf
 * @date: 2024/8/23 12:02
 * @description:
 */
public class DaoCloudLogAppender extends AppenderBase<ILoggingEvent> {

    private final String logStoragePath;

    public DaoCloudLogAppender(String logStoragePath) {
        this.logStoragePath = logStoragePath;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String traceId = MDC.get("traceId");
        String stage = MDC.get("stage");
        if (!StringUtils.hasLength(traceId)) {
            return;
        }
        String filePath = logStoragePath + File.separator + traceId;
        String separator = "-";
        for (String str : stage.split(separator)) {
            filePath += File.separator + str;
        }
        FileUtil.writeUtf8String(eventObject.getFormattedMessage(), filePath);
    }
}
