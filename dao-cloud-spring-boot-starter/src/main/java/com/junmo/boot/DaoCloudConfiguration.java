package com.junmo.boot;

import com.junmo.boot.bootstrap.CenterApplicationRunner;
import com.junmo.boot.bootstrap.ConfigCenterBootstrap;
import com.junmo.boot.bootstrap.RpcClientBootstrap;
import com.junmo.boot.bootstrap.RpcServerBootstrap;
import com.junmo.boot.properties.DaoCloudServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author: sucf
 * @date: 2022/10/28 20:29
 * @description: rpc SpringBoot Auto Configuration
 */
@Configuration
@EnableConfigurationProperties({DaoCloudServerProperties.class})
@ConditionalOnProperty(prefix = "dao-cloud", name = "enable", havingValue = "true")
@Import({DaoCloudServerProperties.class, CenterApplicationRunner.class, RpcServerBootstrap.class, RpcClientBootstrap.class, ConfigCenterBootstrap.class})
public class DaoCloudConfiguration {
}
