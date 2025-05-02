package com.dao.cloud.center.core;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务连接管理
 *
 * @author sucf
 * @date 2025/5/2 21:25
 * @since 1.0.0
 */
public class ServiceConnectorManager {

    /**
     * 服务连接channel
     */
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();


    /**
     * 收集连接
     */
    public void collect(String addressPort, Channel channel) {
        channels.put(addressPort, channel);
    }

    /**
     * 统计当前center节点下连接服务数量
     */
    public Integer count() {
        return channels.size();
    }

    /**
     * 断开连接
     */
    public void disconnection(String addressPort) throws IOException {
        Channel channel = channels.remove(addressPort);
        channel.close();
    }
}
