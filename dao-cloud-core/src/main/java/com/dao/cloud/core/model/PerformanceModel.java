package com.dao.cloud.core.model;


import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2024/6/16 00:00
 * @description: Service node load performance information
 */
@Slf4j
public class PerformanceModel implements Serializable {
    /**
     * cpu %
     */
    private String cpu;

    /**
     * memory %
     */
    private String memory;

    /**
     * io %
     */
    private String io;
}
