package com.junmo.boot.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.core.model.RegisterProxyModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/2/1 17:14
 * @description:
 */
@Slf4j
public class PollClient implements Runnable {

    private Set<RegisterProxyModel> relyProxy;

    public PollClient(Set<RegisterProxyModel> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        for (RegisterProxyModel registerPollModel : relyProxy) {
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    try {
                        String proxy = registerPollModel.getProxy();
                        int version = registerPollModel.getVersion();
                        Set<ChannelClient> oldChannelClients = ClientManager.getClients(proxy, version);
                        Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                        List<ServerNodeModel> serverNodeModels;
                        serverNodeModels = RegistryManager.poll(proxy, version);
                        if (!CollectionUtils.isEmpty(serverNodeModels)) {
                            for (ServerNodeModel serverNodeModel : serverNodeModels) {
                                ChannelClient channelClient = new ChannelClient(proxy, version, serverNodeModel.getIp(), serverNodeModel.getPort());
                                pollChannelClients.add(channelClient);
                            }
                        }
                        // new up server node
                        Set<ChannelClient> newUpChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(pollChannelClients, oldChannelClients);
                        ClientManager.addAll(proxy, version, newUpChannelClients);
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< poll proxy = {}, version = {} server node error >>>>>>>>>>>", registerPollModel.getProxy(), registerPollModel.getVersion(), e);
                    } finally {
                        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 3, TimeUnit.SECONDS);
                    }
                }
            };
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 3, TimeUnit.SECONDS);
        }
    }
}
