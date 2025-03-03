package com.dao.cloud.starter.log;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/9/10 23:57
 */
@Component
public class DaoCloudCustomInterceptor implements HandlerInterceptor {

    private final LogHandlerInterceptor logHandlerInterceptor = new LogHandlerInterceptor();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logHandlerInterceptor.enterCreateTraceId();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
        logHandlerInterceptor.leave();
    }
}
