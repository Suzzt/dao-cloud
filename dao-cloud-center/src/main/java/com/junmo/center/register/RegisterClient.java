package com.junmo.center.register;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterModel;
import com.junmo.core.model.ServerNodeModel;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/13 22:52
 * @description: server register manager
 */
@Slf4j
public class RegisterClient {

    /**
     * server info
     * key: proxy + '#' + version
     * value: nodes
     * key: ip + port
     * value: alive date
     */
    public final static Map<String, Map<String, String>> SERVER_MAP = new ConcurrentHashMap<>();

    /**
     * channel info
     * key:proxy name
     * value:register channel
     */
    public final static Map<String, Map<String, Channel>> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static String makeKey(String proxy, int version) {
        return proxy + "#" + version;
    }

    public static synchronized void register(RegisterModel registerModel) {
        String key = makeKey(registerModel.getProxy(), registerModel.getVersion());
        String ipLinkPort = registerModel.getIpLinkPort();
        Map<String, String> nodeList = SERVER_MAP.get(key);
        if (nodeList == null) {
            add(key, ipLinkPort);
        } else {
            String registerTime = nodeList.get(ipLinkPort);
            if (StringUtils.hasLength(registerTime)) {
                alive(key, ipLinkPort);
            } else {
                add(key, ipLinkPort);
            }
        }
    }

    public static synchronized void add(String key, String ipLinkPort) {
        if (SERVER_MAP.containsKey(key)) {
            Map<String, String> nodeList = SERVER_MAP.get(key);
            nodeList.put(ipLinkPort, DateUtil.now());
        } else {
            Map<String, String> nodeList = Maps.newConcurrentMap();
            nodeList.put(ipLinkPort, DateUtil.now());
            SERVER_MAP.put(key, nodeList);
        }
        log.info(">>>>>>>>>>>> proxy({},{}) register success <<<<<<<<<<<<", key, ipLinkPort);
        // todo notice all clients
    }

    public static synchronized void alive(String key, String ipLinkPort) {
        Map<String, String> nodeList = SERVER_MAP.get(key);
        nodeList.put(ipLinkPort, DateUtil.now());
        log.info(">>>>>>>>>>> alive server proxy({},{}} <<<<<<<<<<<", key, ipLinkPort);
    }

    public static void delete(RegisterModel registerModel) {
        String key = makeKey(registerModel.getProxy(), registerModel.getVersion());
        String ipLinkPort = registerModel.getIpLinkPort();
        Map<String, String> nodeList = SERVER_MAP.get(key);
        nodeList.remove(ipLinkPort);
        log.info(">>>>>>>>>>> down server proxy ({},{}) <<<<<<<<<<<", key, ipLinkPort);
        // todo notice all clients

    }

    public static List<ServerNodeModel> getServers(String proxy) {
        return getServers(proxy, 0);
    }

    public static List<ServerNodeModel> getServers(String proxy, int version) {
        String key = makeKey(proxy, version);
        List<ServerNodeModel> serverNodeModels = Lists.newArrayList();
        if (!StringUtils.hasLength(key)) {
            throw new DaoException("proxy = " + proxy + ", version = " + version + " is null");
        }
        Map<String, String> nodeList = SERVER_MAP.get(key);
        if (!CollectionUtils.isEmpty(nodeList)) {
            for (Map.Entry<String, String> entry : nodeList.entrySet()) {
                String ipLinkPort = entry.getKey();
                ServerNodeModel serverNodeModel = new ServerNodeModel(ipLinkPort);
                serverNodeModels.add(serverNodeModel);
            }
        }
        return serverNodeModels;
    }
}
