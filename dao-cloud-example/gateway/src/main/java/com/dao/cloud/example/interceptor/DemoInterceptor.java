package com.dao.cloud.example.interceptor;

import com.dao.cloud.gateway.intercept.InterceptionResult;
import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.intercept.annotation.GatewayInterceptorRegister;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2024/3/10 09:54
 * @description:
 */
@GatewayInterceptorRegister
@Component
public class DemoInterceptor implements Interceptor {
    @Override
    public InterceptionResult intercept() {
        return new InterceptionResult(false);
    }
}
