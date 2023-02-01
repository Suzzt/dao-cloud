package com.junmo.boot.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.ChannelClient;
import com.junmo.boot.bootstrap.ClientManager;
import com.junmo.boot.bootstrap.RegistryManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ServerNodeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/2/1 17:14
 * @description:
 */
@Slf4j
public class PollClient implements Runnable{

    private Set<String> relyProxy;

    public PollClient(Set<String> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        while (true) {
            // 这里只是兜底方案,目的是整顿这个集群
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.debug("<<<<<<<<<<< thread interrupted... >>>>>>>>>>>", e);
            }
            for (String proxy : relyProxy) {
                Set<ChannelClient> oldChannelClients = ClientManager.getClients(proxy);
                Set<ChannelClient> pollChannelClients = Sets.newLinkedHashSet();
                List<ServerNodeModel> serverNodeModels;
                try {
                    serverNodeModels = RegistryManager.poll(proxy);
                    if (!CollectionUtils.isEmpty(serverNodeModels)) {
                        for (ServerNodeModel serverNodeModel : serverNodeModels) {
                            ChannelClient channelClient = new ChannelClient(proxy, serverNodeModel.getIp(), serverNodeModel.getPort());
                            pollChannelClients.add(channelClient);
                        }
                    }
                    // new up server node
                    Set<ChannelClient> newUpChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(pollChannelClients, oldChannelClients);
                    ClientManager.addAll(proxy, newUpChannelClients);
                    // down server node
                    Set<ChannelClient> downChannelClients = (Set<ChannelClient>) CollectionUtil.subtract(oldChannelClients, pollChannelClients);
                    ClientManager.removeAll(proxy, downChannelClients);
                    //log.info(">>>>>>>>>>> proxy = {} poll server node success <<<<<<<<<<", proxy);
                } catch (InterruptedException e) {
                    log.error("<<<<<<<<<<< poll server node fair >>>>>>>>>>>", e);
                    throw new DaoException(e);
                }
            }
        }
    }
}
