package com.junmo.boot.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.bootstrap.RegistryManager;
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
 * @description:
 */
@Slf4j
public class SyncServerTimer implements Runnable {

    private Set<ProxyProviderModel> relyProxy;

    public SyncServerTimer(Set<ProxyProviderModel> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        for (ProxyProviderModel proxyProviderModel : relyProxy) {
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    try {
                        Set<ChannelClient> oldChannelClients = ClientManager.getClients(proxyProviderModel);
                        Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                        Set<ServerNodeModel> serverNodeModels = RegistryManager.poll(proxyProviderModel);
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                ChannelClient channelClient = new ChannelClient(proxyProviderModel, serverNodeModel.getIp(), serverNodeModel.getPort());
                                pollChannelClients.add(channelClient);
                            }
                            // new up server node
                            oldChannelClients = oldChannelClients == null ? new HashSet<>() : oldChannelClients;
                            Set<ChannelClient> newUpChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(pollChannelClients, oldChannelClients);
                            ClientManager.addAll(proxyProviderModel, newUpChannelClients);
                        }
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< poll proxy = {}, provider = {} server node error >>>>>>>>>>>", proxyProviderModel.getProxy(), proxyProviderModel.getProviderModel(), e);
                    } finally {
                        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 3, TimeUnit.SECONDS);
                    }
                }
            };
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 3, TimeUnit.SECONDS);
        }
    }
}
