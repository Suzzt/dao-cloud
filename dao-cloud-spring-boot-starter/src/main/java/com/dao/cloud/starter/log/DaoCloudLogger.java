package com.dao.cloud.starter.log;

import org.slf4j.MDC;

/**
 * @author: sucf
 * @date: 2024/8/30 17:23
 * @description: dao cloud logger
 */
public class DaoCloudLogger {
    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
