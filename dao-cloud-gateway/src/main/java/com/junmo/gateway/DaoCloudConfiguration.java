package com.junmo.gateway;

import com.junmo.gateway.bootstrap.GatewayBootstrap;
import com.junmo.gateway.properties.GatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * @author: sucf
 * @date: 2023/12/27 17:43
 * @description: Gateway Configuration stater
 */
@Configuration
@EnableConfigurationProperties({GatewayProperties.class})
@ConditionalOnProperty(prefix = "dao-cloud.gateway", name = "enable", havingValue = "true")
@DependsOn({"daoCloudCenterBootstrap", "rpcProviderBootstrap", "rpcConsumerBootstrap", "configCenterBootstrap"})
@Import(GatewayBootstrap.class)
public class DaoCloudConfiguration {
}
