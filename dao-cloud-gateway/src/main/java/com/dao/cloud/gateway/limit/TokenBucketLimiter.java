package com.dao.cloud.gateway.limit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sucf
 * @since 1.0
 * 令牌桶限流
 */
public class TokenBucketLimiter extends Limiter {

    /**
     * 桶的容量
     */
    private final Integer maxTokens;
    /**
     * 生成令牌的速率
     */
    private final Integer refillRate;
    /**
     * 当前令牌数量
     */
    private final AtomicLong availableTokens;
    /**
     * 上次令牌更新时间
     */
    private final AtomicLong lastRefillTimestamp;

    public TokenBucketLimiter(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.availableTokens = new AtomicLong(0);
        this.lastRefillTimestamp = new AtomicLong(System.nanoTime());
    }

    @Override
    public Boolean tryAcquire() {
        refill();
        long currentTokens = availableTokens.get();
        if (currentTokens > 0 && availableTokens.compareAndSet(currentTokens, currentTokens - 1)) {
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        if (now - lastRefillTimestamp.get() > 1e9 / refillRate) {
            long tokensToAdd = (now - lastRefillTimestamp.get()) / (long) (1e9 / refillRate);
            long newTokenCount = Math.min(maxTokens, availableTokens.get() + tokensToAdd);
            availableTokens.set(newTokenCount);
            lastRefillTimestamp.set(now);
        }
    }

    @Override
    public boolean doEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenBucketLimiter)) return false;

        TokenBucketLimiter that = (TokenBucketLimiter) o;

        if (maxTokens != that.maxTokens) return false;
        if (refillRate != that.refillRate) return false;

        return true;
    }

    @Override
    public int doHashCode() {
        int result = (int) (maxTokens ^ (maxTokens >>> 32));
        result = 31 * result + (int) (refillRate ^ (refillRate >>> 32));
        return result;
    }

    @Override
    public String doToString() {
        return "TokenBucketLimiter{" +
                "maxTokens=" + maxTokens +
                ", refillRate=" + refillRate +
                '}';
    }
}