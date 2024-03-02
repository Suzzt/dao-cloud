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
    public LimitModel(Integer limitAlgorithm, Integer limitNumber) {
        this.limitAlgorithm = limitAlgorithm;
        this.limitNumber = limitNumber;
    }

    /**
     * 限流算法
     */
    private Integer limitAlgorithm;

    /**
     * 限流数量
     */
    private Integer limitNumber;

    /**
     * 滑动窗口大小
     * (unit=ms)
     */
    private Long slideWindowSize;
}