package com.junmo.boot.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.RegisterPollModel;
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

    private Set<RegisterPollModel> relyProxy;

    public PollClient(Set<RegisterPollModel> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                for (RegisterPollModel registerPollModel : relyProxy) {
                    String proxy = registerPollModel.getProxy();
                    int version = registerPollModel.getVersion();
                    Set<ChannelClient> oldChannelClients = ClientManager.getClients(proxy, version);
                    Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                    List<ServerNodeModel> serverNodeModels;
                    try {
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
                        // down server node
                        Set<ChannelClient> downChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(oldChannelClients, pollChannelClients);
                        ClientManager.removeAll(proxy, version, downChannelClients);
                    } catch (InterruptedException e) {
                        log.error("<<<<<<<<<<< poll server node fair >>>>>>>>>>>", e);
                        throw new DaoException(e);
                    }
                }
                DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 3, TimeUnit.SECONDS);
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 3, TimeUnit.SECONDS);
    }
}
