package com.dao.cloud.starter.log;

/**
 * @author: sucf
 * @date: 2024/8/30 17:23
 * @description: dao cloud logger
 */
public class DaoCloudLogger {

    private final static ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static String getTraceId() {
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }

    public static void setTraceId(String traceId){
        THREAD_LOCAL.set(traceId);
    }
}
