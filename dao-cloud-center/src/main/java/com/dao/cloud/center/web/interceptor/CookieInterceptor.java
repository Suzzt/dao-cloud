package com.dao.cloud.center.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/07/29 17:22
 * cookie拦截器
 */
@Component
public class CookieInterceptor extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.getCookies() != null && request.getCookies().length > 0) {
            Map<String, Cookie> cookieMap = new HashMap<>();
            for (Cookie ck : request.getCookies()) {
                cookieMap.put(ck.getName(), ck);
            }
            modelAndView.addObject("cookieMap", cookieMap);
        }
        super.postHandle(request, response, handler, modelAndView);
    }
}
