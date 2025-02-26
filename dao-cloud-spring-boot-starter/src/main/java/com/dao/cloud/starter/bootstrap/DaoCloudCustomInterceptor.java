package com.dao.cloud.starter.bootstrap;


import com.dao.cloud.starter.log.LogHandlerInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: sucf
 * @date: 2024/9/10 23:57
 * @description:
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
