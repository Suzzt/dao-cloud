package com.dao.cloud.starter;

import com.dao.cloud.starter.bootstrap.ConfigCenterBootstrap;
import com.dao.cloud.starter.bootstrap.DaoCloudCenterBootstrap;
import com.dao.cloud.starter.bootstrap.RpcConsumerBootstrap;
import com.dao.cloud.starter.properties.DaoCloudCenterProperties;
import com.dao.cloud.starter.properties.DaoCloudServerProperties;
import com.dao.cloud.starter.bootstrap.RpcProviderBootstrap;
import com.dao.cloud.core.converter.StringToCharConverter;
import com.dao.cloud.core.resolver.MethodArgumentResolver;
import com.dao.cloud.core.resolver.MethodArgumentResolverHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;

import java.util.List;


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

    @Bean
    public ConversionService customerConversionService() {
        DateTimeFormatters dateTimeFormatters = new DateTimeFormatters()
            .dateTimeFormat("yyyy-MM-dd HH:mm:ss")
            .dateFormat("yyyy-MM-dd")
            .timeFormat("HH:mm:ss");
        dateTimeFormatters.dateFormat("yyyy-MM-dd");
        ConfigurableConversionService conversionService = new WebConversionService(dateTimeFormatters);
        conversionService.addConverter(new StringToCharConverter());
        return conversionService;
    }

    @Bean
    public MethodArgumentResolverHandler methodArgumentResolverHandler(@Autowired(required = false) ConversionService customerConversionService,
        @Autowired(required = false)List<MethodArgumentResolver> resolverList) {
        // conversionService 可自定义
        MethodArgumentResolverHandler resolverHandler = new MethodArgumentResolverHandler(customerConversionService);
//        如果当前满足不了你的参数解析需求，可以自己扩展，然后可以自己加排序
        resolverHandler.addCustomerResolvers(resolverList);
        return resolverHandler;
    }
}
