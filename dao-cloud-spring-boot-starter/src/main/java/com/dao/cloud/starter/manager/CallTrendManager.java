package com.dao.cloud.starter.manager;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.CallTrendModel;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: sucf
 * @date: 2024/7/13 22:14
 * @description: Call Trend Manager
 */
@Slf4j
public class CallTrendManager {

    private final static Map<ProxyProviderModel, Map<String, AtomicLong>> CALL_DATA = Maps.newConcurrentMap();

    public static void syncCall(ProxyProviderModel proxyProviderModel, String methodName) {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("Unable to connect to center");
        }
        AtomicLong atomicLong = CALL_DATA.get(proxyProviderModel).get(methodName);
        CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, methodName, atomicLong.get());
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.CALL_TREND_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, callTrendModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< send call data error >>>>>>>>>", future.cause());
            }
        });
    }
}
