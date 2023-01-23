package com.junmo.boot.banlance.impl;

import cn.hutool.core.util.RandomUtil;
import com.junmo.boot.banlance.DaoLoadBalance;
import com.junmo.boot.channel.ChannelClient;
import io.netty.channel.Channel;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/11 22:39
 * @description:
 */
public class RandomLoadBalanceImpl extends DaoLoadBalance {
    @Override
    public Channel route(Set<ChannelClient> availableChannelClients) {
        ChannelClient[] clients = availableChannelClients.toArray(new ChannelClient[availableChannelClients.size()]);
        int index = RandomUtil.randomInt(availableChannelClients.size());
        return clients[index].getChannel();
    }
}