package com.dao.cloud.gateway.intercept;

/**
 * @author sucf
 * @since 1.0
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
