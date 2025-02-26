package com.dao.cloud.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sucf
 * @since 1.0
 * 限流信息
 */
@Data
public class LimitModel implements Serializable {

    public LimitModel(Integer limitAlgorithm, Long slideDateWindowSize, Integer slideWindowMaxRequestCount, Integer tokenBucketMaxSize, Integer tokenBucketRefillRate, Integer leakyBucketCapacity, Integer leakyBucketRefillRate) {
        this.limitAlgorithm = limitAlgorithm;
        this.slideDateWindowSize = slideDateWindowSize;
        this.slideWindowMaxRequestCount = slideWindowMaxRequestCount;
        this.tokenBucketMaxSize = tokenBucketMaxSize;
        this.tokenBucketRefillRate = tokenBucketRefillRate;
        this.leakyBucketCapacity = leakyBucketCapacity;
        this.leakyBucketRefillRate = leakyBucketRefillRate;
    }

    /**
     * 限流算法
     */
    private Integer limitAlgorithm;

    /* ============================ 限流参数 ============================ */

    /**
     * 滑动时间窗口大小
     * (unit=ms)
     */
    private Long slideDateWindowSize;

    /**
     * 时间窗口内允许的最大请求数
     */
    private Integer slideWindowMaxRequestCount;

    /**
     * 令牌桶的最大令牌数
     */
    private Integer tokenBucketMaxSize;

    /**
     * 每秒新增的令牌数
     */
    private Integer tokenBucketRefillRate;

    /**
     * 漏桶的容量
     */
    private Integer leakyBucketCapacity;

    /**
     * 令牌填充的速度(每秒)
     */
    private Integer leakyBucketRefillRate;

    /* ============================ 限流参数 ============================ */

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LimitModel{");
        sb.append("limitAlgorithm=").append(limitAlgorithm);
        sb.append(", slideDateWindowSize=").append(slideDateWindowSize);
        sb.append(", slideWindowMaxRequestCount=").append(slideWindowMaxRequestCount);
        sb.append(", tokenBucketMaxSize=").append(tokenBucketMaxSize);
        sb.append(", tokenBucketRefillRate=").append(tokenBucketRefillRate);
        sb.append(", leakyBucketCapacity=").append(leakyBucketCapacity);
        sb.append(", leakyBucketRefillRate=").append(leakyBucketRefillRate);
        sb.append('}');
        return sb.toString();
    }
}