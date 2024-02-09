package com.junmo.boot;

import com.junmo.boot.bootstrap.ConfigCenterBootstrap;
import com.junmo.boot.bootstrap.DaoCloudCenterBootstrap;
import com.junmo.boot.bootstrap.GatewayBootstrap;
import com.junmo.boot.bootstrap.RpcConsumerBootstrap;
import com.junmo.boot.bootstrap.RpcProviderBootstrap;
import com.junmo.boot.properties.DaoCloudCenterProperties;
import com.junmo.boot.properties.DaoCloudServerProperties;
import com.junmo.core.resolver.MethodArgumentResolverHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;


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
        RpcConsumerBootstrap.class, ConfigCenterBootstrap.class, GatewayBootstrap.class})
public class DaoCloudConfiguration {

    @Bean
    public MethodArgumentResolverHandler methodArgumentResolverHandler(@Autowired(required = false) ConversionService conversionService) {
        // conversionService 可自定义
        MethodArgumentResolverHandler resolverHandler = new MethodArgumentResolverHandler(conversionService);
//        如果当前满足不了你的参数解析需求，可以自己扩展，然后可以自己加排序
//        resolverHandler.addCustomerResolver();
        return resolverHandler;
    }
}
