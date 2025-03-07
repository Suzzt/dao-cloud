package com.dao.cloud.gateway;

import com.dao.cloud.core.model.ProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.NetUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import com.dao.cloud.gateway.global.GlobalGatewayExceptionHandler;
import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.intercept.annotation.GatewayInterceptorRegister;
import com.dao.cloud.gateway.properties.DaoCloudGatewayProperties;
import com.dao.cloud.gateway.timer.GatewayPullServiceTimer;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.banlance.impl.HashLoadBalance;
import com.dao.cloud.starter.banlance.impl.RandomLoadBalance;
import com.dao.cloud.starter.banlance.impl.RoundLoadBalance;
import com.dao.cloud.starter.manager.RegistryManager;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Gateway AutoConfiguration
 *
 * @author sucf
 * @date 2023/12/27 17:43
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "dao-cloud.gateway", name = "enable", havingValue = "true")
@EnableConfigurationProperties(DaoCloudGatewayProperties.class)
public class GatewayAutoConfiguration implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private final DaoCloudGatewayProperties gatewayProperties;

    public GatewayAutoConfiguration(DaoCloudGatewayProperties daoCloudGatewayProperties) {
        this.gatewayProperties = daoCloudGatewayProperties;
    }

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
        interceptors.sort(Comparator.comparingInt(interceptor -> AnnotationUtils.findAnnotation(interceptor.getClass(), GatewayInterceptorRegister.class).order()));
        return new DaoCloudGatewayDispatcher(daoLoadBalance, interceptors);
    }

    @Bean
    public GlobalGatewayExceptionHandler globalGatewayExceptionHandler() {
        return new GlobalGatewayExceptionHandler();
    }

    @Bean
    public DaoLoadBalance daoLoadBalance() {
        String loadBalance = gatewayProperties.getLoadBalance();
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // load all service && start thread task pull service
        loadPull();
        // registry center
        registry();
    }

    /**
     * load and start a new pull service task
     */
    public void loadPull() {
        Thread timer = new Thread(new GatewayPullServiceTimer());
        ThreadPoolFactory.GLOBAL_THREAD_POOL.execute(timer);
    }

    /**
     * registry gateway node to center
     */
    public void registry() {
        RegisterProviderModel gatewayNodeModel = new RegisterProviderModel();
        gatewayNodeModel.setProxy(DaoCloudConstant.GATEWAY_PROXY);
        Set<ProviderModel> providerModels = new HashSet<>();
        int version = gatewayProperties.getVersion() == null ? 0 : gatewayProperties.getVersion();
        ProviderModel providerModel = new ProviderModel(DaoCloudConstant.GATEWAY, version);
        providerModels.add(providerModel);
        gatewayNodeModel.setProviderModels(providerModels);
        gatewayNodeModel.setServerNodeModel(new ServerNodeModel(NetUtil.getLocalIp(), DaoCloudConstant.GATEWAY_PORT));
        RegistryManager.registry(gatewayNodeModel);
    }
}
