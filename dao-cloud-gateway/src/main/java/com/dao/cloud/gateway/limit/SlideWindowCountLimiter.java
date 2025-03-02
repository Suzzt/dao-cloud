package com.dao.cloud.gateway.limit;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/12/27 17:55
 * 滑动计数限流
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

    @Override
    public boolean doEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlideWindowCountLimiter that = (SlideWindowCountLimiter) o;
        return windowSizeInMillis == that.windowSizeInMillis && maxRequestCount == that.maxRequestCount;
    }

    @Override
    public int doHashCode() {
        return Objects.hash(windowSizeInMillis, maxRequestCount);
    }

    @Override
    public String doToString() {
        final StringBuffer sb = new StringBuffer("SlideWindowCountLimiter{");
        sb.append("windowSizeInMillis=").append(windowSizeInMillis);
        sb.append(", maxRequestCount=").append(maxRequestCount);
        sb.append('}');
        return sb.toString();
    }
}
