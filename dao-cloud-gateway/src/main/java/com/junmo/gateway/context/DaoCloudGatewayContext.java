package com.junmo.gateway.context;

/**
 * @author: sucf
 * @date: 2024/1/16 11:07
 * @description: 网关上下文信息
 */
public class DaoCloudGatewayContext {

    public static ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 从网关上下文中获取自定义数据
     *
     * @return
     */
    public static Object get() {
        return null;
    }

    /**
     * 在网关上下文填充自定义数据
     */
    public static void set(Object object) {

    }

}
