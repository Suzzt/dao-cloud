package com.junmo.gateway.bootstrap.thread;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.GatewayPullServiceMarkModel;
import com.junmo.core.model.GatewayServiceNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.DaoTimer;
import com.junmo.gateway.manager.GatewayServiceManager;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2024/1/3 23:25
 * @description:
 */
@Slf4j
public class PullServiceTimer implements Runnable {
    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    // 从注册中心拉取所有服务节点数据
                    DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new GatewayPullServiceMarkModel());
                    DefaultPromise<GatewayServiceNodeModel> promise = new DefaultPromise<>(CenterChannelManager.getChannel().eventLoop());
                    CenterChannelManager.getChannel().writeAndFlush(daoMessage).addListener(future -> {
                        if (!future.isSuccess()) {
                            promise.setFailure(future.cause());
                        }
                    });
                    if (!promise.await(10, TimeUnit.SECONDS)) {
                        log.error("<<<<<<<<<<<<<< Gateway pull service node info timeout >>>>>>>>>>>>>>");
                        throw new DaoException("promise await timeout");
                    }
                    if (promise.isSuccess()) {
                        GatewayServiceManager.reset(promise.getNow().getRegistryServiceNodes());
                    } else {
                        throw new DaoException(promise.cause());
                    }
                } catch (Exception e) {
                    log.error("Gateway pull service node error", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 10, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 10, TimeUnit.SECONDS);
    }
}
