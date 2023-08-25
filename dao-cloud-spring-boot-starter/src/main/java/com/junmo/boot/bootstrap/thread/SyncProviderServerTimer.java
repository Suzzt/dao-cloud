package com.junmo.boot.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.boot.bootstrap.manager.RegistryManager;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/2/1 17:14
 * @description: 服务节点拉取定时器
 */
@Slf4j
public class SyncProviderServerTimer implements Runnable {

    private Set<ProxyProviderModel> relyProxy;

    public SyncProviderServerTimer(Set<ProxyProviderModel> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        for (ProxyProviderModel proxyProviderModel : relyProxy) {
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    try {
                        Set<ServerNodeModel> oldProviderNodes = ClientManager.getProviderNodes(proxyProviderModel);
                        Set<ServerNodeModel> pullProviderNodes = Sets.newLinkedHashSet();
                        Set<ServerNodeModel> serverNodeModels = RegistryManager.pull(proxyProviderModel);
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                pullProviderNodes.add(serverNodeModel);
                            }
                            // new up server node
                            oldProviderNodes = oldProviderNodes == null ? new HashSet<>() : oldProviderNodes;
                            Set<ServerNodeModel> newUpProviderNodes = (Set<ServerNodeModel>) CollectionUtil.subtract(pullProviderNodes, oldProviderNodes);
                            ClientManager.add(proxyProviderModel, newUpProviderNodes);
                        }
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< pull proxy = {}, provider = {} server node error >>>>>>>>>>>", proxyProviderModel.getProxy(), proxyProviderModel.getProviderModel(), e);
                    } finally {
                        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 3, TimeUnit.SECONDS);
                    }
                }
            };
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 3, TimeUnit.SECONDS);
        }
    }
}