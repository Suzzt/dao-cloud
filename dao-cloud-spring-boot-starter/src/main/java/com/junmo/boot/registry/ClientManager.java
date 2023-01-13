package com.junmo.boot.registry;

import com.junmo.boot.annotation.DaoReference;
import com.junmo.boot.proxy.RpcProxyFactory;
import com.junmo.core.exception.DaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


/**
 * @author: sucf
 * @date: 2023/1/12 11:11
 * @description:
 */
@Slf4j
@Component
public class ClientManager implements SmartInstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean, BeanFactoryAware {

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(DaoReference.class)) {
                // valid
                Class iface = field.getType();
                if (!iface.isInterface()) {
                    throw new DaoException("dao-cloud, reference(DaoReference) must be interface.");
                }
                DaoReference daoReference = field.getAnnotation(DaoReference.class);
                // get proxyObj
                Object serviceProxy;
                try {
                    serviceProxy = RpcProxyFactory.build(iface);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // set bean
                field.setAccessible(true);
                field.set(bean, serviceProxy);
                log.info(">>>>>>>>>>> dao-cloud, invoker init reference bean success. proxy = {}, beanName = {}");
            }
        });
        System.out.println(bean);
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
