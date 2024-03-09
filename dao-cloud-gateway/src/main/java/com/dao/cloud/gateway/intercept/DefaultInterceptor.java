package com.dao.cloud.gateway.intercept;

/**
 * @author: sucf
 * @date: 2024/3/9 14:48
 * @description: 默认拦截器
 */
public class DefaultInterceptor implements Interceptor {
    @Override
    public boolean intercept() {
        return false;
    }
}
