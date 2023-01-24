package com.junmo.boot.bootstrap;

import com.junmo.boot.annotation.DaoService;
import com.junmo.boot.bootstrap.thread.ServerNetty;
import com.junmo.boot.properties.DaoCloudProperties;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.DaoCallback;
import com.junmo.core.model.RpcRequestModel;
import com.junmo.core.model.RpcResponseModel;
import com.junmo.core.netty.serialize.SerializeStrategyFactory;
import com.junmo.core.util.SystemUtil;
import com.junmo.core.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author sucf
 * @date 2022/12/29 16:30
 * @description:
 */
@Slf4j
public class RpcServerBootstrap implements ApplicationContextAware, InitializingBean, DisposableBean {

    private Map<String, Object> localServiceCache = new HashMap<>();

    /**
     * do invoke when server start
     */
    private DaoCallback startCallback;
    /**
     * do invoke when server stop
     */
    private DaoCallback stopCallback;

    private Thread thread;

    private boolean isServerStarted;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        //scan annotation DaoService
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(DaoService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            return;
        }
        isServerStarted = true;
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new DaoException("dao-cloud-rpc service(DaoService) must inherit interface.");
            }
            String interfaces = serviceBean.getClass().getInterfaces()[0].getName();
            addServiceCache(interfaces, serviceBean);
        }
    }

    /**
     * prepare
     *
     * @throws Exception
     */
    public void prepare() throws Exception {
        DaoCloudProperties.serializerType = SerializeStrategyFactory.getSerializeType(DaoCloudProperties.serializer);
        if (!(DaoCloudProperties.corePoolSize > 0 && DaoCloudProperties.maxPoolSize > 0 && DaoCloudProperties.maxPoolSize >= DaoCloudProperties.corePoolSize)) {
            DaoCloudProperties.corePoolSize = 60;
            DaoCloudProperties.maxPoolSize = 300;
        }

        if (DaoCloudProperties.serverPort <= 0) {
            DaoCloudProperties.serverPort = SystemUtil.getAvailablePort(65535);
        }

        if (!StringUtils.hasLength(DaoCloudProperties.proxy)) {
            throw new DaoException("'dao-cloud.proxy' config must it");
        }
    }

    /**
     * start
     */
    public void start() throws Exception {
        if (!isServerStarted) {
            return;
        }
        prepare();
        // make thread pool
        ThreadPoolExecutor threadPoolProvider = ThreadPoolFactory.makeThreadPool("provider", DaoCloudProperties.corePoolSize, DaoCloudProperties.maxPoolSize);
        thread = new ServerNetty(threadPoolProvider, this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * add server
     *
     * @param interfaces
     * @param serviceBean
     */
    private void addServiceCache(String interfaces, Object serviceBean) {
        localServiceCache.put(interfaces, serviceBean);
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
        Object serviceBean = localServiceCache.get(requestModel.getInterfaceName());

        // valid
        if (serviceBean == null) {
            responseModel.setExceptionValue(new DaoException("no method exists"));
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
            log.error("dao-cloud provider invokeService error.", t);
            responseModel.setExceptionValue(new DaoException(t));
        }

        return responseModel;
    }

    @Override
    public void destroy() throws Exception {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        log.debug(">>>>>>>>>>> dao-cloud-rpc provider server destroy <<<<<<<<<<<<");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
