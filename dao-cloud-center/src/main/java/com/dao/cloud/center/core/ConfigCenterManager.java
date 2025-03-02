package com.dao.cloud.center.core;

import com.dao.cloud.center.core.storage.Persistence;
import com.dao.cloud.center.web.vo.ConfigVO;
import com.dao.cloud.core.model.ConfigModel;
import com.dao.cloud.core.model.ProxyConfigModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/11 22:57
 * config manager
 */
@Slf4j
public class ConfigCenterManager {

    /**
     * 缓存配置
     */
    private Map<ProxyConfigModel, String> cache;

    /**
     * 配置信息的持久化
     */
    @Resource
    private Persistence persistence;

    /**
     * 初始化拉取配置中心的配置信息到本地服务的缓存内存中
     */
    public void init() {
        cache = persistence.loadConfig();
    }

    /**
     * 获取所有日志
     *
     * @return
     */
    public Map<ProxyConfigModel, String> getFullConfig() {
        return cache;
    }

    /**
     * add or update config content
     *
     * @param proxyConfigModel
     * @param jsonValue
     */
    public synchronized void save(ProxyConfigModel proxyConfigModel, String jsonValue) {
        cache.put(proxyConfigModel, jsonValue);
        // persistence config data
        ConfigModel config = new ConfigModel();
        config.setProxyConfigModel(proxyConfigModel);
        config.setConfigValue(jsonValue);
        persistence.storage(config);
        // Notification subscription service for callback
        Set<Channel> subscribeChannels = ConfigChannelManager.getSubscribeChannel(proxyConfigModel);
        if (!CollectionUtils.isEmpty(subscribeChannels)) {
            for (Channel channel : subscribeChannels) {
                ConfigModel configModel = new ConfigModel();
                configModel.setProxyConfigModel(proxyConfigModel);
                configModel.setConfigValue(jsonValue);
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, configModel);
                channel.writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("<<<<<<<<<<< pushing config information to subscriber({}) failed >>>>>>>>>>>", channel);
                    }
                });
            }
        }
    }

    /**
     * delete config
     *
     * @param proxyConfigModel
     */
    public synchronized void delete(ProxyConfigModel proxyConfigModel) {
        cache.remove(proxyConfigModel);
        persistence.delete(proxyConfigModel);
    }

    /**
     * get config value
     *
     * @param proxyConfigModel
     */
    public String getConfigValue(ProxyConfigModel proxyConfigModel) {
        return cache.get(proxyConfigModel);
    }

    /**
     * 获取配置信息
     *
     * @param proxy
     * @param key
     * @param version
     * @return
     */
    public List<ConfigVO> getConfigVO(String proxy, String key, Integer version) {
        List<ConfigVO> result = Lists.newArrayList();
        for (Map.Entry<ProxyConfigModel, String> entry : cache.entrySet()) {
            ConfigVO configVO = new ConfigVO();
            ProxyConfigModel proxyConfigModel = entry.getKey();
            if (StringUtils.hasLength(proxy) && !proxyConfigModel.getProxy().equals(proxy)) {
                continue;
            }
            if (StringUtils.hasLength(key) && !proxyConfigModel.getKey().equals(key)) {
                continue;
            }
            if (version != null && version!=proxyConfigModel.getVersion()) {
                continue;
            }
            configVO.setProxy(proxyConfigModel.getProxy());
            configVO.setKey(proxyConfigModel.getKey());
            configVO.setVersion(proxyConfigModel.getVersion());
            configVO.setContent(entry.getValue());
            result.add(configVO);
        }
        return result;
    }

    /**
     * 配置数量
     *
     * @return
     */
    public int size() {
        return cache.size();
    }
}
