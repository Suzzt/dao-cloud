package com.junmo.config.register;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author: sucf
 * @date: 2022/11/13 22:57
 * @description: server node info
 */
@Data
public class ServerInfo {
    /**
     * ip地址
     * map key:ip+port value:heart beat time
     */
    private Map<String, String> registerNodeMap;

    public ServerInfo(String ipLinkPort) {
        Map<String, String> registerNodeMap = Maps.newConcurrentMap();
        registerNodeMap.put(ipLinkPort, DateUtil.now());
        this.registerNodeMap = registerNodeMap;
    }

    public void addNode(String ipLinkPort) {
        registerNodeMap.put(ipLinkPort, null);
    }
}
