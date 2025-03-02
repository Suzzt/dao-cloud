package com.dao.cloud.center.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/9/21 23:32
 */
@Data
public class ProxyStatisticsVO {
    private List<String> dimension;
    private List<Integer> measure;

    public ProxyStatisticsVO(List<String> dimension, List<Integer> measure) {
        this.dimension = dimension;
        this.measure = measure;
    }
}
