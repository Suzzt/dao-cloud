package com.dao.cloud.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2024/2/5 14:11
 * @description: 限流信息
 */
@Data
public class LimitModel implements Serializable {

    public LimitModel(Integer limitAlgorithm, Long slideDateWindowSize, Integer slideWindowMaxRequestCount, Integer tokenBucketMaxSize, Integer tokenBucketRefillRate, Integer leakyBucketCapacity, int leakyBucketRefillRate) {
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
}