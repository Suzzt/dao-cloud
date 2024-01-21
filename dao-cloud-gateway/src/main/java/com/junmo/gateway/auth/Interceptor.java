package com.junmo.gateway.auth;

/**
 * @author: sucf
 * @date: 2023/12/27 17:58
 * @description: Request Auth Interceptor
 */
public interface Interceptor {

    /**
     * 开启拦截行动
     *
     * @return
     */
    boolean action();

}
