package com.dao.cloud.center.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/9/8 22:18
 */
@Data
public class LogMeta implements Comparable {

    private String stage;
    private String node;
    private Long happenTime;

    @Override
    public int compareTo(Object o) {
        return happenTime > ((LogMeta) o).happenTime ? 1 : -1;
    }
}
