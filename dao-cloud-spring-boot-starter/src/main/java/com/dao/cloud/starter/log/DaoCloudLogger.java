package com.dao.cloud.starter.log;

import org.slf4j.MDC;

/**
 * @author sucf
 * @since 1.0
 * dao cloud logger
 */
public class DaoCloudLogger {
    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
