package com.dao.cloud.gateway.limit;

/**
 * @author: sucf
 * @date: 2023/12/27 17:55
 * @description: 计数限流
 */
public class CountLimiter extends Limiter {
    @Override
    public Boolean allow() {
        return true;
    }
}
