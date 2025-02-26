package com.dao.cloud.gateway.intercept;

/**
 * @author: sucf
 * @date: 2023/12/27 17:58
 * @description: Request Interceptor
 */
public interface Interceptor {

    /**
     * intercept logic
     *
     * @return
     */
    InterceptionResult intercept();
}
