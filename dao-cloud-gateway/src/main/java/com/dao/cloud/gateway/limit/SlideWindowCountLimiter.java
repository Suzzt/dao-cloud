package com.dao.cloud.gateway.limit;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/12/27 17:55
 * @description: 滑动计数限流
 */
public class SlideWindowCountLimiter extends Limiter {

    /**
     * 时间窗口内的请求记录，用于保存请求的时间戳
     */
    private ConcurrentLinkedQueue<Long> slidingWindow = new ConcurrentLinkedQueue<>();

    /**
     * 时间窗口大小
     */
    private final long windowSizeInMillis;

    /**
     * 时间窗口内允许的最大请求数
     */
    private final int maxRequestCount;

    public SlideWindowCountLimiter(int maxRequestCount, long windowSizeInMillis) {
        this.maxRequestCount = maxRequestCount;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    @Override
    public Boolean tryAcquire() {
        long now = System.currentTimeMillis();
        // 移除时间窗口之外的请求记录
        while (!slidingWindow.isEmpty() && now - slidingWindow.peek() > windowSizeInMillis) {
            slidingWindow.poll();
            threshold.decrementAndGet();
        }
        slidingWindow.add(now);
        threshold.incrementAndGet();
        return threshold.get() < maxRequestCount;
    }

    public static void main(String[] args) {
        // 1秒钟最大请求次数
        final int MAX_REQUESTS_PER_SECOND = 7;
        System.out.println(TimeUnit.SECONDS.toMillis(1));
        SlideWindowCountLimiter rateLimiter = new SlideWindowCountLimiter(MAX_REQUESTS_PER_SECOND, TimeUnit.SECONDS.toMillis(1));

        // 模拟请求
        for (int i = 0; i < 10; i++) {
            if (rateLimiter.tryAcquire()) {
                System.out.println("Request " + (i + 1) + ": Allowed");
            } else {
                System.out.println("Request " + (i + 1) + ": Denied");
            }
            try {
                // 100毫秒1次请求
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
