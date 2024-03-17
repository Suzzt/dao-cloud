package com.dao.cloud.starter.context;

/**
 * @author: sucf
 * @date: 2024/03/13 23:29
 * @description:
 */
public class DaoCloudContext<T> {

    /**
     * ThreadLocal storage to keep the context data thread-confined.
     */
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    private static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    public static void set(Object object) {
        THREAD_LOCAL.set(object);
    }
}