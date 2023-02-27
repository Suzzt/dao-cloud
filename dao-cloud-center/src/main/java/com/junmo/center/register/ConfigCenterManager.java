package com.junmo.center.register;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.center.web.vo.ConfigVO;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageModelTypeManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/11 22:57
 * @description: config info manager
 */
@Slf4j
public class ConfigCenterManager {

    private static Map<ProxyConfigModel, String> CONFIG = Maps.newConcurrentMap();

    public static List<ConfigVO> getConfigVO(String proxy, String key) {
        List<ConfigVO> result = Lists.newArrayList();
        for (Map.Entry<ProxyConfigModel, String> entry : CONFIG.entrySet()) {
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

    /**
     * add or update config content
     *
     * @param proxyConfigModel
     * @param jsonValue
     */
    public static synchronized void update(ProxyConfigModel proxyConfigModel, String jsonValue) {
        CONFIG.put(proxyConfigModel, jsonValue);
        Set<Channel> subscribeChannels = ConfigChannelManager.getSubscribeChannel(proxyConfigModel);
        if (!CollectionUtils.isEmpty(subscribeChannels)) {
            for (Channel channel : subscribeChannels) {
                ConfigModel configModel = new ConfigModel();
                configModel.setProxyConfigModel(proxyConfigModel);
                configModel.setConfigValue(jsonValue);
                DaoMessage daoMessage = new DaoMessage((byte) 0, MessageModelTypeManager.POLL_REGISTRY_CONFIG_RESPONSE_MESSAGE, (byte) 0, configModel);
                channel.writeAndFlush(daoMessage).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("<<<<<<<<<<< pushing config information to subscriber({}) failed >>>>>>>>>>>", channel);
                    }
                });
            }
        }
    }

    /**
     * get config value
     *
     * @param proxyConfigModel
     */
    public static String getConfigValue(ProxyConfigModel proxyConfigModel) {
        return CONFIG.get(proxyConfigModel);
    }
}