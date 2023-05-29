package com.junmo.center.core;

import com.google.common.collect.Lists;
import com.junmo.core.expand.Persistence;
import com.junmo.center.web.vo.ConfigVO;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/11 22:57
 * @description: config manager
 */
@Slf4j
public class ConfigCenterManager {

    private Map<ProxyConfigModel, String> cache;

    @Resource
    private Persistence persistence;

    @Autowired
    public ConfigCenterManager(Persistence persistence) {
        cache = persistence.load();
    }

    /**
     * add or update config content
     *
     * @param proxyConfigModel
     * @param jsonValue
     */
    public synchronized void update(ProxyConfigModel proxyConfigModel, String jsonValue) {
        cache.put(proxyConfigModel, jsonValue);
        // persistence config data
        ConfigModel config = new ConfigModel();
        config.setProxyConfigModel(proxyConfigModel);
        config.setConfigValue(jsonValue);
        persistence.storage(config);
        Set<Channel> subscribeChannels = ConfigChannelManager.getSubscribeChannel(proxyConfigModel);
        if (!CollectionUtils.isEmpty(subscribeChannels)) {
            for (Channel channel : subscribeChannels) {
                ConfigModel configModel = new ConfigModel();
                configModel.setProxyConfigModel(proxyConfigModel);
                configModel.setConfigValue(jsonValue);
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.PULL_REGISTRY_CONFIG_RESPONSE_MESSAGE, (byte) 0, configModel);
                channel.writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("<<<<<<<<<<< pushing config information to subscriber({}) failed >>>>>>>>>>>", channel);
                    }
                });
            }
        }
    }

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

    public List<ConfigVO> getConfigVO(String proxy, String key) {
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
            configVO.setProxy(proxyConfigModel.getProxy());
            configVO.setKey(proxyConfigModel.getKey());
            configVO.setVersion(proxyConfigModel.getVersion());
            configVO.setValue(entry.getValue());
            result.add(configVO);
        }
        return result;
    }
}
