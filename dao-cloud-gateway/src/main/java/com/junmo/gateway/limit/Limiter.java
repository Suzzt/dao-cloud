package com.junmo.gateway.limit;

/**
 * @author: sucf
 * @date: 2023/12/27 17:46
 * @description: 抽象限流器
 */
public abstract class Limiter {

    /**
     * 限流是否通过
     *
     * @return true: 通过限流, false: 限流拦截
     */
    public abstract Boolean allow();
}
