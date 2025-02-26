package com.dao.cloud.starter.log;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

/**
 * @author: sucf
 * @date: 2024/9/3 23:03
 * @description: 日志拦截器
 */
public class LogHandlerInterceptor {

    public void enter(String traceId) {
        MDC.put("traceId", traceId);
    }

    public void enterCreateTraceId() {
        MDC.put("traceId", IdUtil.getSnowflake(2, 2).nextIdStr());
    }

    public void leave() {
        MDC.remove("traceId");
    }
}
