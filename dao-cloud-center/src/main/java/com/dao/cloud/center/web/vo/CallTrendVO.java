package com.dao.cloud.center.web.vo;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/7/15 21:14
 */
@Data
public class CallTrendVO {
    private String methodName;
    private Long count;
    public CallTrendVO(String methodName, Long count) {
        this.methodName = methodName;
        this.count = count;
    }
}
