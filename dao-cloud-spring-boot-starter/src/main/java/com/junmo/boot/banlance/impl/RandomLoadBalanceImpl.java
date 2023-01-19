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
public class RandomLoadBalanceImpl implements DaoLoadBalance {
    @Override
    public Channel route(Set<ChannelClient> channelClients) {
        ChannelClient[] clients = channelClients.toArray(new ChannelClient[channelClients.size()]);
        int index = RandomUtil.randomInt(channelClients.size());
        return clients[index].getChannel();
    }
}
