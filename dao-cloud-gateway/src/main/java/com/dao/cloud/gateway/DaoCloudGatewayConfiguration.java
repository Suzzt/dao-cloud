package com.dao.cloud.gateway;

import com.dao.cloud.gateway.global.GlobalGatewayExceptionHandler;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.banlance.impl.RoundLoadBalance;
import com.dao.cloud.starter.bootstrap.DaoCloudCenterBootstrap;
import com.dao.cloud.starter.properties.DaoCloudCenterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: sucf
 * @date: 2023/12/27 17:43
 * @description: Gateway Configuration starter
 */
@Configuration
@ConditionalOnProperty(prefix = "dao-cloud.gateway", name = "enable", havingValue = "true")
@Import({DaoCloudCenterProperties.class, DaoCloudCenterBootstrap.class, GatewayBootstrap.class})
public class DaoCloudGatewayConfiguration {
    @Bean
    public Dispatcher dispatcher(DaoLoadBalance daoLoadBalance) {
        return new Dispatcher(daoLoadBalance);
    }

    @Bean
    public GlobalGatewayExceptionHandler globalGatewayExceptionHandler() {
        return new GlobalGatewayExceptionHandler();
    }

    @Bean
    public DaoLoadBalance daoLoadBalance() {
        return new RoundLoadBalance();
    }
}
