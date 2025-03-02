package com.dao.cloud.gateway.limit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/4/9 11:09
 * 漏桶限流
 */
public class LeakyBucketLimiter extends Limiter {

    // 漏桶的容量
    private final long capacity;
    // 漏出速率
    private final long leakRate;
    // 上次漏水时间
    private final AtomicLong lastLeakTimestamp;
    // 当前水量
    private final AtomicInteger water;

    public LeakyBucketLimiter(long capacity, long leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.lastLeakTimestamp = new AtomicLong(System.currentTimeMillis());
        this.water = new AtomicInteger(0);
    }

    @Override
    public Boolean tryAcquire() {
        // 先漏水
        long now = System.currentTimeMillis();
        long leaks = (now - lastLeakTimestamp.get()) / 1000 * leakRate;
        if (leaks > 0) {
            // 更新漏水时间
            lastLeakTimestamp.set(now);
            // 减去已经漏掉的水
            water.addAndGet((int) -leaks);
        }
        // 桶已经干了，重置水量
        if (water.get() < 0) {
            water.set(0);
        }
        // 尝试加水
        if (water.incrementAndGet() <= capacity) {
            // 还没满，成功获取一个令牌
            return true;
        } else {
            // 水溢出了，拒绝访问
            water.decrementAndGet();
            return false;
        }
    }

    @Override
    public boolean doEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeakyBucketLimiter)) return false;

        LeakyBucketLimiter that = (LeakyBucketLimiter) o;

        return capacity == that.capacity && leakRate == that.leakRate;
    }

    @Override
    public int doHashCode() {
        int result = (int) (capacity ^ (capacity >>> 32));
        result = 31 * result + (int) (leakRate ^ (leakRate >>> 32));
        return result;
    }

    @Override
    public String doToString() {
        return "LeakyBucketLimiter{" +
                "capacity=" + capacity +
                ", leakRate=" + leakRate +
                '}';
    }
}