package com.junmo.config.register;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ServerNodeModel;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
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
public class Register {

    /**
     * server info
     * key:proxy name
     * value:nodes
     * key:ip+port
     * value:date
     */
    public final static Map<String, Map<String, String>> SERVER_MAP = new ConcurrentHashMap<>();

    /**
     * channel info
     * key:proxy name
     * value:register channel
     */
    public final static Map<String, Map<String, Channel>> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static synchronized void register(String proxy, String ipLinkPort) {
        Map<String, String> nodeList = SERVER_MAP.get(proxy);
        if (nodeList == null) {
            add(proxy, ipLinkPort);
        } else {
            String registerTime = nodeList.get(ipLinkPort);
            if (StringUtils.hasLength(registerTime)) {
                alive(proxy, ipLinkPort);
            } else {
                add(proxy, ipLinkPort);
            }
        }
    }

    public static synchronized void add(String proxy, String ipLinkPort) {
        if (SERVER_MAP.containsKey(proxy)) {
            Map<String, String> nodeList = SERVER_MAP.get(proxy);
            nodeList.put(ipLinkPort, DateUtil.now());
        } else {
            Map<String, String> nodeList = Maps.newConcurrentMap();
            nodeList.put(ipLinkPort, DateUtil.now());
            SERVER_MAP.put(proxy, nodeList);
        }
        log.info(">>>>>>>>>>>> proxy({},{}) register success <<<<<<<<<<<<", proxy, ipLinkPort);
        // todo notice all clients
    }

    public static synchronized void alive(String proxy, String ipLinkPort) {
        Map<String, String> nodeList = SERVER_MAP.get(proxy);
        nodeList.put(ipLinkPort, DateUtil.now());
        log.info(">>>>>>>>>>> alive server proxy({},{}} <<<<<<<<<<<", proxy, ipLinkPort);
    }

    public static void delete(String proxy, String ipLinkPort) {
        Map<String, String> nodeList = SERVER_MAP.get(proxy);
        nodeList.remove(ipLinkPort);
        log.info(">>>>>>>>>>> down server proxy ({},{}) <<<<<<<<<<<", proxy, ipLinkPort);
        // todo notice all clients

    }

    public static List<ServerNodeModel> getServers(String proxy) {
        List<ServerNodeModel> serverNodeModels = Lists.newArrayList();
        if (!StringUtils.hasLength(proxy)) {
            throw new DaoException("proxy = " + proxy + " is null");
        }
        Map<String, String> nodeList = SERVER_MAP.get(proxy);
        if (nodeList == null) {
            throw new DaoException("not exist proxy = " + proxy);
        }
        for (Map.Entry<String, String> entry : nodeList.entrySet()) {
            String ipLinkPort = entry.getKey();
            ServerNodeModel serverNodeModel = new ServerNodeModel(ipLinkPort);
            serverNodeModels.add(serverNodeModel);
        }
        return serverNodeModels;
    }
}
