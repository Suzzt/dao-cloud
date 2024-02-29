package com.dao.cloud.core.util;

import com.google.common.collect.Maps;
import io.netty.util.concurrent.Promise;

import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/9/8 23:33
 * @description:
 */
public class LongPromiseBuffer {
    /**
     * Long promise
     */
    private static volatile Map<Long, Promise<Object>> PROMISE_RESOURCE;

    /**
     * 单例获取资源
     *
     * @return
     */
    public static Map<Long, Promise<Object>> getInstance() {
        if (PROMISE_RESOURCE == null) {
            synchronized (LongPromiseBuffer.class) {
                if (PROMISE_RESOURCE == null) {
                    PROMISE_RESOURCE = Maps.newConcurrentMap();
                }
            }
        }
        return PROMISE_RESOURCE;
    }
}
