package com.dao.cloud.starter.utils;

import com.dao.cloud.core.util.GsonUtils;
import com.dao.cloud.starter.manager.CenterChannelManager;
import com.dao.cloud.starter.unit.ConfigCallBack;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.dao.cloud.core.model.ProxyConfigModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.ProxyConfigPromiseBuffer;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/26 18:32
 * dao config
 */
@Slf4j
public class DaoCloudConfig {

    /**
     * local cache config
     */
    private static final Map<ProxyConfigModel, String> CONFIG_OBJECT = Maps.newConcurrentMap();

    /**
     * config subscribe callback
     */
    private static final Map<ProxyConfigModel, List<ConfigCallBack>> CONFIG_SUBSCRIBERS = Maps.newConcurrentMap();

    /**
     * update the config object
     *
     * @param proxyConfigModel
     * @param jsonValue
     * @return
     */
    public static void update(ProxyConfigModel proxyConfigModel, String jsonValue) {
        CONFIG_OBJECT.put(proxyConfigModel, jsonValue);
    }

    /**
     * refresh system config value
     *
     * @param proxyConfigModel
     * @param jsonValue
     */
    public static void refresh(ProxyConfigModel proxyConfigModel, String jsonValue) {
        CONFIG_OBJECT.put(proxyConfigModel, jsonValue);
        List<ConfigCallBack> configCallBacks = CONFIG_SUBSCRIBERS.get(proxyConfigModel);
        if (!CollectionUtils.isEmpty(configCallBacks)) {
            for (ConfigCallBack configCallBack : configCallBacks) {
                configCallBack.callback(proxyConfigModel, GsonUtils.fromJson(jsonValue, configCallBack.getClazz()));
            }
        }
    }

    /**
     * 订阅config center
     *
     * @param proxyConfigModel 配置key
     * @param callback         回调请实现它
     */
    public static synchronized <T> T subscribe(ProxyConfigModel proxyConfigModel, ConfigCallBack<T> callback) {
        List<ConfigCallBack> configCallBacks = CONFIG_SUBSCRIBERS.get(proxyConfigModel);
        if (CollectionUtils.isEmpty(configCallBacks)) {
            configCallBacks = Lists.newArrayList();
            configCallBacks.add(callback);
            CONFIG_SUBSCRIBERS.put(proxyConfigModel, configCallBacks);
        } else {
            configCallBacks.add(callback);
        }
        return getConf(proxyConfigModel, callback.getClazz());
    }

    /**
     * config center
     *
     * @param proxy
     * @param key
     * @param version
     * @param callback
     */
    public static synchronized <T> T subscribe(String proxy, String key, int version, ConfigCallBack<T> callback) {
        ProxyConfigModel proxyConfigModel = new ProxyConfigModel();
        proxyConfigModel.setProxy(proxy);
        proxyConfigModel.setKey(key);
        proxyConfigModel.setVersion(version);
        return subscribe(proxyConfigModel, callback);
    }

    /**
     * config center
     *
     * @param proxy
     * @param key
     * @param callback
     */
    public static synchronized <T> T subscribe(String proxy, String key, ConfigCallBack<T> callback) {
        return subscribe(proxy, key, 0, callback);
    }

    /**
     * get config
     *
     * @param proxyConfigModel
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getConf(ProxyConfigModel proxyConfigModel, Class<T> clazz) {
        String jsonValue = CONFIG_OBJECT.get(proxyConfigModel);
        if (!StringUtils.hasLength(jsonValue)) {
            // no hit cache
            DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.PULL_REGISTRY_CONFIG_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, proxyConfigModel);
            Promise<String> promise = new DefaultPromise<>(CenterChannelManager.getChannel().eventLoop());
            ProxyConfigPromiseBuffer.getInstance().put(proxyConfigModel, promise);
            CenterChannelManager.getChannel().writeAndFlush(daoMessage).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<<<<<< failed to fetch config({}) from config center error >>>>>>>>>>>>>>", proxyConfigModel, future.cause());
                }
            });
            try {
                if (!promise.await(1, TimeUnit.SECONDS)) {
                    log.error("<<<<<<<<<<<<<< pull config({}) timeout >>>>>>>>>>>>>>", proxyConfigModel);
                    return null;
                }
                if (promise.isSuccess()) {
                    String configJson = promise.getNow();
                    if (!StringUtils.hasLength(configJson)) {
                        return null;
                    }
                    jsonValue = configJson;
                    // put in the cache
                    update(proxyConfigModel, jsonValue);
                } else {
                    log.error("<<<<<<<<<<<<<< pull config({}) fail. >>>>>>>>>>>>>>", proxyConfigModel, promise.cause());
                    return null;
                }
            } catch (InterruptedException e) {
                log.error("<<<<<<<<<<<<<< pull config({}) fail >>>>>>>>>>>>>>", proxyConfigModel, e);
                return null;
            }
        }
        try {
            if (String.class.equals(clazz)) {
                return (T) jsonValue;
            }
            return (T) GsonUtils.fromJson(jsonValue, clazz);
        } catch (Exception e) {
            log.error("<<<<<<<<<<<<<< get config ({}) error >>>>>>>>>>>>>>", proxyConfigModel, e);
        }
        return null;
    }

    /**
     * get config
     *
     * @param proxy
     * @param key
     * @param version
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getConf(String proxy, String key, int version, Class<T> clazz) {
        ProxyConfigModel proxyConfigModel = new ProxyConfigModel();
        proxyConfigModel.setProxy(proxy);
        proxyConfigModel.setKey(key);
        proxyConfigModel.setVersion(version);
        return getConf(proxyConfigModel, clazz);
    }

    /**
     * get config
     *
     * @param proxy
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getConf(String proxy, String key, Class<T> clazz) {
        return getConf(proxy, key, 0, clazz);
    }
}
