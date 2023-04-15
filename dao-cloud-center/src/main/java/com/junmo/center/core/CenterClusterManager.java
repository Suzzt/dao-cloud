package com.junmo.center.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.junmo.core.model.CenterModel;
import com.junmo.core.model.ServerNodeModel;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/3/12 22:27
 * @description:
 */
public class CenterClusterManager {

    /**
     * cluster center node
     * key: cluster
     * value: center server info
     */
    private final static Map<String, List<ServerNodeModel>> CLUSTER = Maps.newConcurrentMap();

    public synchronized void reshard(){

    }

    public void transfer(){

    }

    public static void syncNode(){

    }

    public synchronized static void acceptance(CenterModel centerModel) {
        String cluster = centerModel.getCluster();
        ServerNodeModel serverNodeModel = centerModel.getServerNodeModel();
        if (CLUSTER.containsKey(cluster)) {
            List<ServerNodeModel> serverNodeModels = CLUSTER.get(cluster);
            serverNodeModels.add(serverNodeModel);
        } else {
            List<ServerNodeModel> serverNodeModels = Lists.newArrayList();
            serverNodeModels.add(serverNodeModel);
            CLUSTER.put(cluster, serverNodeModels);
        }
    }

    public synchronized static void remove(CenterModel centerModel) {
        String cluster = centerModel.getCluster();
        ServerNodeModel serverNodeModel = centerModel.getServerNodeModel();
        List<ServerNodeModel> serverNodeModels = CLUSTER.get(cluster);
        if (CollectionUtils.isEmpty(serverNodeModels)) {
            return;
        }
        serverNodeModels.remove(centerModel);
    }
}
