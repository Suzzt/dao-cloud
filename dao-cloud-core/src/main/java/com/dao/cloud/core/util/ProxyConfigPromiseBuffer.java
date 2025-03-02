package com.dao.cloud.core.util;

import com.google.common.collect.Maps;
import com.dao.cloud.core.model.ProxyConfigModel;
import io.netty.util.concurrent.Promise;

import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/9/13 15:09
 */
public class ProxyConfigPromiseBuffer {
    /**
     * ProxyConfigModel promise
     */
    private static volatile Map<ProxyConfigModel, Promise<String>> PROMISE_RESOURCE;

    /**
     * 单例获取资源
     *
     * @return
     */
    public static Map<ProxyConfigModel, Promise<String>> getInstance() {
        if (PROMISE_RESOURCE == null) {
            synchronized (ProxyConfigPromiseBuffer.class) {
                if (PROMISE_RESOURCE == null) {
                    PROMISE_RESOURCE = Maps.newConcurrentMap();
                }
            }
        }
        return PROMISE_RESOURCE;
    }
}
