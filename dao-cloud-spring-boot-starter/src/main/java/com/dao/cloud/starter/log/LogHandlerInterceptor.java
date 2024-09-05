package com.dao.cloud.starter.log;

import org.slf4j.MDC;

/**
 * @author: sucf
 * @date: 2024/9/3 23:03
 * @description: 日志拦截器
 */
public class LogHandlerInterceptor {

    public void enter(String traceId, String stage) {
        MDC.put("traceId", traceId);
        MDC.put("stage", stage);
    }

    public void leave() {
        MDC.remove("traceId");
        MDC.remove("stage");
    }
}
