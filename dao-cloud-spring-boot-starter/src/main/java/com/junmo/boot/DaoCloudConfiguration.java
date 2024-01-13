package com.junmo.boot;

import com.junmo.boot.bootstrap.DaoCloudCenterBootstrap;
import com.junmo.boot.bootstrap.ConfigCenterBootstrap;
import com.junmo.boot.bootstrap.RpcConsumerBootstrap;
import com.junmo.boot.bootstrap.RpcProviderBootstrap;
import com.junmo.boot.properties.DaoCloudCenterProperties;
import com.junmo.boot.properties.DaoCloudServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author: sucf
 * @date: 2022/10/28 20:29
 * @description: dao-cloud SpringBoot Auto Configuration
 */
@Configuration
@EnableConfigurationProperties({DaoCloudServerProperties.class})
@ConditionalOnProperty(prefix = "dao-cloud", name = "enable", havingValue = "true")
@Import({DaoCloudServerProperties.class, DaoCloudCenterProperties.class,
        DaoCloudCenterBootstrap.class, RpcProviderBootstrap.class,
        RpcConsumerBootstrap.class, ConfigCenterBootstrap.class})
public class DaoCloudConfiguration {
}
