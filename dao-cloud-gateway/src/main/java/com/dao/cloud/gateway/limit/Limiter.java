package com.dao.cloud.gateway.limit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sucf
 * @since 1.0
 * 抽象限流器
 * Be sure to rewrite the equals and hashcode methods,
 * because when pulling the gateway configuration,
 * these two methods are used to determine whether the configuration has been changed.
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

    @Override
    public boolean equals(Object o) {
        return doEquals(o);
    }

    public abstract boolean doEquals(Object o);

    @Override
    public int hashCode() {
        return doHashCode();
    }

    public abstract int doHashCode();

    @Override
    public String toString() {
        return doToString();
    }

    public abstract String doToString();
}
