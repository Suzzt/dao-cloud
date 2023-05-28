package com.junmo.center.bootstarp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: sucf
 * @date: 2023/3/12 17:58
 * @description:
 */
@ConfigurationProperties(prefix = "dao-cloud.center.cluster")
@Data
public class DaoCloudClusterCenterProperties {
    private String ip;
}
