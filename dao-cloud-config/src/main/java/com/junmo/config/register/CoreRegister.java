package com.junmo.config.register;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2022/11/13 22:52
 * @description: server register manager
 */
@Slf4j
public class CoreRegister {

    static {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * server info
     * key:proxy name
     * value:nodes
     */
    public final static Map<String, ServerNode> serverMap = new ConcurrentHashMap<>();

    public static synchronized void add(String proxyName, String ipAddress) {
        if (CoreRegister.serverMap.containsKey(proxyName)) {
            ServerNode serverNode = CoreRegister.serverMap.get(proxyName);
            serverNode.addNode(ipAddress);
        } else {
            CoreRegister.serverMap.put(proxyName, new ServerNode(ipAddress));
        }
        log.info("register server success proxy name = {}, ip = {}", proxyName, ipAddress);
    }

    public static void delete(String proxyName) {
        serverMap.remove(proxyName);
    }

    public static synchronized void alive(String proxyName, String ipAddress) {
        log.info("register server success proxy name = {}, ip = {}", proxyName, ipAddress);
    }
}
