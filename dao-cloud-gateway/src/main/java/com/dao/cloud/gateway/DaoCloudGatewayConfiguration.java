package com.dao.cloud.gateway;

import com.dao.cloud.gateway.bootstrap.GatewayBootstrap;
import com.dao.cloud.gateway.global.GlobalGatewayExceptionHandler;
import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.intercept.annotation.GatewayInterceptorRegister;
import com.dao.cloud.gateway.properties.DaoCloudGatewayProperties;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.banlance.impl.HashLoadBalance;
import com.dao.cloud.starter.banlance.impl.RandomLoadBalance;
import com.dao.cloud.starter.banlance.impl.RoundLoadBalance;
import com.dao.cloud.starter.properties.DaoCloudCenterProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/12/27 17:43
 * @description: Gateway Configuration starter
 */
@Configuration
@ConditionalOnProperty(prefix = "dao-cloud.gateway", name = "enable", havingValue = "true")
@Import({DaoCloudCenterProperties.class, GatewayBootstrap.class})
public class DaoCloudGatewayConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public DaoCloudGatewayDispatcher dispatcher(DaoLoadBalance daoLoadBalance) {
        // 获取所有注解GatewayInterceptorRegister的Beans
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(GatewayInterceptorRegister.class);
        List<Interceptor> interceptors = new ArrayList<>();
        // 遍历这些Beans，检查它们是否实现了Interceptor接口
        for (Object bean : beans.values()) {
            if (bean instanceof Interceptor) {
                interceptors.add((Interceptor) bean);
            }
        }
        // 在这里对拦截器列表进行排序
        interceptors.sort(Comparator.comparingInt(interceptor ->
                AnnotationUtils.findAnnotation(interceptor.getClass(), GatewayInterceptorRegister.class).order()));
        return new DaoCloudGatewayDispatcher(daoLoadBalance, interceptors);
    }

    @Bean
    public GlobalGatewayExceptionHandler globalGatewayExceptionHandler() {
        return new GlobalGatewayExceptionHandler();
    }

    @Bean
    public DaoLoadBalance daoLoadBalance() {
        String loadBalance = DaoCloudGatewayProperties.loadBalance;
        if (!StringUtils.hasLength(loadBalance)) {
            return new RoundLoadBalance();
        }
        switch (loadBalance) {
            case "random":
                return new RandomLoadBalance();
            case "hash":
                return new HashLoadBalance();
            default:
                // 默认为轮询
                return new RoundLoadBalance();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
