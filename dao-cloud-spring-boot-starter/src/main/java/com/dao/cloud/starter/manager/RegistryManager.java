package com.dao.cloud.starter.manager;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.RegisterProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.DaoTimer;
import com.dao.cloud.starter.handler.CenterServerMessageHandler;
import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2022/11/19 17:53
 * @description:
 */
@Slf4j
public class RegistryManager {

    /**
     * pull register server node by center
     *
     * @param proxyProviderModel
     * @return
     * @throws Exception
     */
    public static Set<ServerNodeModel> pull(ProxyProviderModel proxyProviderModel) throws Exception {
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_REGISTRY_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, proxyProviderModel);
        DefaultPromise<Set<ServerNodeModel>> promise = new DefaultPromise<>(CenterChannelManager.getChannel().eventLoop());
        CenterServerMessageHandler.PROMISE_MAP.put(proxyProviderModel, promise);
        CenterChannelManager.getChannel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                promise.setFailure(future.cause());
            }
        });
        if (!promise.await(8, TimeUnit.SECONDS)) {
            log.error("<<<<<<<<<<<<<< pull server node info timeout >>>>>>>>>>>>>>");
            throw new DaoException("promise await timeout");
        }
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new DaoException(promise.cause());
        }
    }

    /**
     * registry provider server
     *
     * @param registerProviderModel
     */
    public static void registry(RegisterProviderModel registerProviderModel) {
        send(registerProviderModel);
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    send(registerProviderModel);
                } catch (Exception e) {
                    log.error("<<<<<<<<< send register server exception >>>>>>>>", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 5, TimeUnit.SECONDS);
    }

    /**
     * 发送注册请求
     *
     * @param registerProviderModel
     */
    private static void send(RegisterProviderModel registerProviderModel) throws DaoException {
        Channel channel = CenterChannelManager.getChannel();
        if (channel == null) {
            throw new DaoException("connect config center error");
        }
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.REGISTRY_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, registerProviderModel);
        channel.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<< send register server error >>>>>>>>>", future.cause());
            }
        });
    }
}
