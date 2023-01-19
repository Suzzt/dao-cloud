package com.junmo.core.util;

import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * @author: sucf
 * @date: 2022/12/29 23:11
 * @description:
 */
public class ThreadPoolFactory {
    static {
        GLOBAL_THREAD_POOL = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> new Thread(r, "global thread pool"));
        SCHEDULED_THREAD_POOL = new ScheduledThreadPoolExecutor(10, r -> new Thread(r, "schedule thread pool"),
                new RejectedExecutionHandler() {
                    @SneakyThrows
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new Exception("dao-cloud-rpc schedule Thread pool is exhausted!");
                    }
                });
    }

    public static ThreadPoolExecutor GLOBAL_THREAD_POOL;

    public static ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL;

    /**
     * make thread pool
     *
     * @param serverType
     * @param corePoolSize
     * @param maxPoolSize
     * @return
     */
    public static ThreadPoolExecutor makeThreadPool(String serverType, int corePoolSize, int maxPoolSize) {
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                r -> new Thread(r, "dao-cloud-rpc, " + serverType + "-serverHandlerPool-" + r.hashCode()),
                new RejectedExecutionHandler() {
                    @SneakyThrows
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new Exception("dao-cloud-rpc " + serverType + " Thread pool is exhausted!");
                    }
                });
        return serverHandlerPool;
    }

}