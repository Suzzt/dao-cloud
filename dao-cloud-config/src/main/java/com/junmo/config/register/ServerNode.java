package com.junmo.config.register;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @author: sucf
 * @date: 2022/11/13 22:57
 * @description: server node info
 */
@Data
public class ServerNode {
    /**
     * ip地址
     * map ip：alive
     */
    private Map<String, RegisterNode> registerNodeMap;

    public ServerNode(String ipAddress) {
        Map<String, RegisterNode> registerNodeMap = Maps.newConcurrentMap();
        RegisterNode registerNode = new RegisterNode();
        registerNode.setAlive(true);
        registerNodeMap.put(ipAddress, registerNode);
        this.registerNodeMap = registerNodeMap;
    }

    public void addNode(String ipAddress){
        registerNodeMap.put(ipAddress,new RegisterNode("xxxxx",0,true));
    }
}
