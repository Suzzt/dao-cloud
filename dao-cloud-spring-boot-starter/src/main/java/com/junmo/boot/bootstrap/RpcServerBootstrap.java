package com.junmo.boot.bootstrap;

import com.google.common.collect.Sets;
import com.junmo.boot.annotation.ConditionalOnUseAnnotation;
import com.junmo.boot.annotation.DaoService;
import com.junmo.boot.bootstrap.thread.Server;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import com.junmo.core.util.SystemUtil;
import com.junmo.core.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sucf
 * @date 2022/12/29 16:30
 * @description: rpc server startup
 */
@Slf4j
@ConditionalOnUseAnnotation(annotation = DaoService.class)
public class RpcServerBootstrap implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    /**
     * local class objects
     * key: instance + '#' +version
     * value: object bean
     */
    private final Map<ProviderModel, Object> localServiceCache = new HashMap<>();

    private Thread thread;

    public Set<ProviderModel> getProviders() {
        return Sets.newHashSet(localServiceCache.keySet());
    }

    /**
     * start
     */
    public void start() {
        DaoCloudProperties.serializerType = SerializeStrategyFactory.getSerializeType(DaoCloudProperties.serializer);
        if (!(DaoCloudProperties.corePoolSize > 0 && DaoCloudProperties.maxPoolSize > 0 && DaoCloudProperties.maxPoolSize >= DaoCloudProperties.corePoolSize)) {
            DaoCloudProperties.corePoolSize = 60;
            DaoCloudProperties.maxPoolSize = 300;
        }

        if (DaoCloudProperties.serverPort <= 0) {
            try {
                DaoCloudProperties.serverPort = SystemUtil.getAvailablePort(65535);
            } catch (Exception e) {
                throw new DaoException(e);
            }
        }

        if (!StringUtils.hasLength(DaoCloudProperties.proxy)) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", DaoCloudProperties.corePoolSize, DaoCloudProperties.maxPoolSize);
        thread = new Server(threadPoolProvider, this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * invoke method
     *
     * @param requestModel
     * @return
     */
    public RpcResponseModel doInvoke(RpcRequestModel requestModel) {
        //  make response
        RpcResponseModel responseModel = new RpcResponseModel();
        responseModel.setSequenceId(requestModel.getSequenceId());

        // match service bean
        ProviderModel providerModel = new ProviderModel(requestModel.getProvider(), requestModel.getVersion());
        Object serviceBean = localServiceCache.get(providerModel);

        // valid
        if (serviceBean == null) {
            responseModel.setErrorMessage("provider not exists method");
            return responseModel;
        }

        try {
            // invoke method
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = requestModel.getMethodName();
            Class<?>[] parameterTypes = requestModel.getParameterTypes();
            Object[] parameters = requestModel.getParameterValue();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            responseModel.setReturnValue(result);
        } catch (Throwable t) {
            log.error("<<<<<<<<<<< dao-cloud provider invokeService error >>>>>>>>>>>>", t);
            responseModel.setErrorMessage(t.getMessage());
        }

        return responseModel;
    }

    @Override
    public void destroy() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        log.debug(">>>>>>>>>>> dao-cloud-rpc provider server destroy <<<<<<<<<<<<");
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
            ProviderModel providerModel = new ProviderModel();
            String interfaces = serviceBean.getClass().getInterfaces()[0].getSimpleName();
            String provider = StringUtils.hasLength(daoService.provider()) ? daoService.provider() : interfaces;
            providerModel.setProvider(provider);
            providerModel.setVersion(daoService.version());
            localServiceCache.put(providerModel, serviceBean);
        }
        start();
    }
}
