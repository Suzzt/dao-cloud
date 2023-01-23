package com.junmo.boot;

import com.junmo.boot.bootstrap.RpcClientBootstrap;
import com.junmo.boot.bootstrap.RpcServerBootstrap;
import com.junmo.boot.properties.DaoCloudProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author: sucf
 * @date: 2022/10/28 20:29
 * @description: rpc SpringBoot Auto Configuration
 */
@Configuration
@EnableConfigurationProperties({DaoCloudProperties.class})
@ConditionalOnProperty(prefix = "dao-cloud", name = "enable", havingValue = "true")
@Slf4j
public class DaoCloudConfiguration {

    @Resource
    private DaoCloudProperties daoCloudProperties;

    @Bean
    public RpcServerBootstrap server() {
        RpcServerBootstrap rpcServerBootstrap = new RpcServerBootstrap();
        return rpcServerBootstrap;
    }

    @Bean
    public RpcClientBootstrap client() {
        RpcClientBootstrap bootstrap = new RpcClientBootstrap();
        return bootstrap;
    }

}
