package com.dao.cloud.gateway.limit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sucf
 * @date: 2023/12/27 17:46
 * @description: 抽象限流器
 */
public abstract class Limiter {

    /**
     * 记录请求数量
     */
    protected AtomicInteger threshold;

    /**
     * 限流是否通过
     *
     * @return true: 通过限流, false: 限流拦截
     */
    public abstract Boolean tryAcquire();
}
