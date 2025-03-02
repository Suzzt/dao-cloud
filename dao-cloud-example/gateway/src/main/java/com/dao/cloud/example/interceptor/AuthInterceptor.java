package com.dao.cloud.example.interceptor;

import com.dao.cloud.gateway.intercept.InterceptionResult;
import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.intercept.annotation.GatewayInterceptorRegister;
import org.springframework.stereotype.Component;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/3/10 09:54
 * Here is an example permission filtering interceptor.
 */
@GatewayInterceptorRegister
@Component
public class AuthInterceptor implements Interceptor {
    @Override
    public InterceptionResult intercept() {
        // Filter specific permission logic...
        // return new InterceptionResult(false);
        // ......
        return new InterceptionResult(true);
    }
}
