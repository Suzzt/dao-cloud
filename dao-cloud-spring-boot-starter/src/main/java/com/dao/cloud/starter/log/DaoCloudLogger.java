package com.dao.cloud.starter.log;

import org.slf4j.MDC;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/8/30 17:23
 * dao cloud logger
 */
public class DaoCloudLogger {
    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
