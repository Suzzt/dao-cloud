package com.junmo.config.register;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/13 22:52
 * @description: server register manager
 */
@Slf4j
public class RegisterManager {

    /**
     * server info
     * key:proxy name
     * value:nodes
     */
    public final static Map<String, ServerInfo> serverMap = new ConcurrentHashMap<>();

    /**
     * channel info
     * key:proxy name
     * value:register channel
     */
    public final static Map<String, Map<String, Channel>> channelMap = new ConcurrentHashMap<>();

    public static synchronized void register(String proxy, String ipLinkPort) {
        ServerInfo serverInfo = serverMap.get(proxy);
        if (serverInfo == null) {
            add(proxy, ipLinkPort);
        } else {
            Map<String, String> registerNodeMap = serverInfo.getRegisterNodeMap();
            String registerTime = registerNodeMap.get(ipLinkPort);
            if (StringUtils.hasLength(registerTime)) {
                alive(proxy, ipLinkPort);
            } else {
                add(proxy, ipLinkPort);
            }
        }
    }

    public static synchronized void add(String proxy, String ipLinkPort) {
        if (serverMap.containsKey(proxy)) {
            ServerInfo serverInfo = serverMap.get(proxy);
            serverInfo.addNode(ipLinkPort);
        } else {
            serverMap.put(proxy, new ServerInfo(ipLinkPort));
        }
        log.debug(">>>>>>>>>>>register server proxy name = {}, ipLinkPort = {}<<<<<<<<<<<", proxy, ipLinkPort);
    }

    public static synchronized void alive(String proxy, String ipLinkPort) {
        ServerInfo serverInfo = serverMap.get(proxy);
        Map<String, String> registerNodeMap = serverInfo.getRegisterNodeMap();
        registerNodeMap.put(ipLinkPort, DateUtil.now());
        log.debug(">>>>>>>>>>>alive server proxy name = {}, ipLinkPort = {}<<<<<<<<<<<", proxy, ipLinkPort);
    }

    public static void delete(String proxy, String ipLinkPort) {
        ServerInfo serverInfo = serverMap.get(proxy);
        Map<String, String> registerNodeMap = serverInfo.getRegisterNodeMap();
        registerNodeMap.remove(ipLinkPort);
        log.debug(">>>>>>>>>>>down server proxy name = {}, ipLinkPort = {}<<<<<<<<<<<", proxy, ipLinkPort);
    }
}
