package com.dao.cloud.gateway.intercept;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/12/27 17:58
 * Request Interceptor
 */
public interface Interceptor {

    /**
     * intercept logic
     *
     * @return
     */
    InterceptionResult intercept();
}
