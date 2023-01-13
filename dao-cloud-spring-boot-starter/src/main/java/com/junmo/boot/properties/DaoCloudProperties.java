package com.junmo.boot.properties;

import com.junmo.boot.serializer.Serializer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2022/12/29 21:30
 * @description: dao cloud config
 */
@ConfigurationProperties(prefix = "dao-cloud")
@Data
public class DaoCloudProperties {
    private String proxy;
    private int corePoolSize;
    private int maxPoolSize;
    private int port;
    private Class<? extends Serializer> serializer;
}
