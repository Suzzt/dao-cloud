package com.dao.cloud.core.model;


import lombok.Data;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2024/6/16 00:00
 * @description: Service node load performance information
 */
@Data
public class PerformanceModel implements Serializable {
    /**
     * 当前jvm使用率 cpu %
     */
    private String cpu;

    /**
     * 当前jvm使用率 memory %
     */
    private String memory;

    /**
     * io %
     */
    private String io;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PerformanceModel{");
        sb.append("cpu='").append(cpu).append('\'');
        sb.append(", memory='").append(memory).append('\'');
        sb.append(", io='").append(io).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
