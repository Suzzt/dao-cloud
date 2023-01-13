//package com.junmo.boot.registry;
//
//import com.junmo.boot.properties.DaoCloudProperties;
//import com.junmo.boot.serializer.Serializer;
//import com.junmo.core.model.DaoCallback;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.context.ApplicationContextAware;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author: sucf
// * @date: 2023/1/11 15:57
// * @description:
// */
//public abstract class RpcManager implements ApplicationContextAware, InitializingBean, DisposableBean {
//    @Resource
//    protected DaoCloudProperties daoCloudProperties;
//
//    protected Serializer serializer;
//
//    protected Map<String, Object> localServiceCache = new HashMap<>();
//
//    /**
//     * do invoke when server start
//     */
//    protected DaoCallback startCallback;
//
//    /**
//     * do invoke when server stop
//     */
//    protected DaoCallback stopCallback;
//
//    protected Thread thread;
//
//}
