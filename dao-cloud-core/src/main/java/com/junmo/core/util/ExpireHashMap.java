package com.junmo.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/7/11 16:56
 * @description: 过期数据类(利用HashMap来实现)
 * 在创建对象时,会开启一个定时任务用来定时清理过期元素,因此清理数据相对来说不是那么准时!
 */
@Slf4j
public class ExpireHashMap<T> {
    private Map<T, Long> map;
    ScheduledExecutorService executor;

    /**
     * @param initialCapacity 初始化容量
     * @param time            定时任务时间
     * @param timeUnit        定时任务单位
     */
    public ExpireHashMap(int initialCapacity, int time, TimeUnit timeUnit) {
        map = new HashMap(initialCapacity);
        executor = new ScheduledThreadPoolExecutor(1);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis() / 1_000;
                for (Object obj : map.entrySet()) {
                    Map.Entry<T, Long> entry = (Map.Entry<T, Long>) obj;
                    Long time = entry.getValue();
                    if (currentTime > time) {
                        map.remove(entry.getKey());
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(task, 0, time, timeUnit);
    }

    /**
     * add element
     *
     * @param t
     * @param seconds
     */
    public void add(T t, int seconds) {
        long timestampSeconds = System.currentTimeMillis() / 1_000;
        map.put(t, timestampSeconds + seconds);
    }

    public boolean exists(T t) {
        return map.containsKey(t);
    }

    public void destroyed() {
        executor.shutdown();
    }
}
