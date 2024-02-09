package com.junmo.gateway;

import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.banlance.impl.RoundLoadBalance;
import com.junmo.boot.bootstrap.DaoCloudCenterBootstrap;
import com.junmo.boot.properties.DaoCloudCenterProperties;
import com.junmo.gateway.bootstrap.GatewayBootstrap;
import com.junmo.gateway.limit.CountLimiter;
import com.junmo.gateway.limit.Limiter;
import com.junmo.gateway.properties.GatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: sucf
 * @date: 2023/12/27 17:43
 * @description: Gateway Configuration starter
 */
@Configuration
@EnableConfigurationProperties({GatewayProperties.class})
@ConditionalOnProperty(prefix = "dao-cloud.gateway", name = "enable", havingValue = "true")
@Import({DaoCloudCenterProperties.class, DaoCloudCenterBootstrap.class, GatewayBootstrap.class})
public class DaoCloudGatewayConfiguration {
    @Bean
    public Dispatcher dispatcher(Limiter limiter, DaoLoadBalance daoLoadBalance) {
        return new Dispatcher(limiter, daoLoadBalance);
    }

    @Bean
    public Limiter limiter() {
        return new CountLimiter();
    }

    @Bean
    public DaoLoadBalance daoLoadBalance() {
        return new RoundLoadBalance();
    }
}
