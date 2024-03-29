package com.dao.cloud.starter.bootstrap;

import com.dao.cloud.starter.annotation.ConditionalOnUseAnnotation;
import com.dao.cloud.starter.annotation.DaoService;
import com.dao.cloud.starter.properties.DaoCloudServerProperties;
import com.dao.cloud.starter.bootstrap.manager.ServiceManager;
import com.dao.cloud.starter.bootstrap.thread.Server;
import com.dao.cloud.starter.bootstrap.unit.ServiceInvoker;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.netty.serialize.SerializeStrategyFactory;
import com.dao.cloud.core.resolver.MethodArgumentResolverHandler;
import com.dao.cloud.core.util.SystemUtil;
import com.dao.cloud.core.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sucf
 * @date 2022/12/29 16:30
 * @description: rpc provider startup
 */
@Slf4j
@ConditionalOnUseAnnotation(annotation = DaoService.class)
public class RpcProviderBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private MethodArgumentResolverHandler methodArgumentResolverHandler;
    /**
     * start
     */
    public void start() {
        if (!(DaoCloudServerProperties.corePoolSize > 0 && DaoCloudServerProperties.maxPoolSize > 0 && DaoCloudServerProperties.maxPoolSize >= DaoCloudServerProperties.corePoolSize)) {
            DaoCloudServerProperties.corePoolSize = 60;
            DaoCloudServerProperties.maxPoolSize = 300;
        }

        if (DaoCloudServerProperties.serverPort <= 0) {
            try {
                DaoCloudServerProperties.serverPort = SystemUtil.getAvailablePort(65535);
            } catch (Exception e) {
                throw new DaoException(e);
            }
        }

        if (!StringUtils.hasLength(DaoCloudServerProperties.proxy)) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", DaoCloudServerProperties.corePoolSize, DaoCloudServerProperties.maxPoolSize);
        new Server(threadPoolProvider, methodArgumentResolverHandler).start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            return;
        }
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
            DaoService daoService = serviceBean.getClass().getAnnotation(DaoService.class);
            String interfaces = serviceBean.getClass().getInterfaces()[0].getName();
            String provider = StringUtils.hasLength(daoService.provider()) ? daoService.provider() : interfaces;
            ServiceInvoker serviceInvoker = new ServiceInvoker(SerializeStrategyFactory.getSerializeType(daoService.serializable().getName()), serviceBean);
            ServiceManager.addService(provider, daoService.version(), serviceInvoker);
        }
        start();
    }
}
