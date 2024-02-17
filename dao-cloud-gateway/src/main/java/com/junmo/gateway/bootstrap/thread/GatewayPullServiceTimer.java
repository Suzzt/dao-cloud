package com.junmo.gateway.bootstrap.thread;

import com.junmo.boot.bootstrap.manager.CenterChannelManager;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.GatewayPullServiceMarkModel;
import com.junmo.core.model.GatewayServiceNodeModel;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.DaoTimer;
import com.junmo.gateway.global.GatewayServiceConfig;
import com.junmo.gateway.hanlder.PullServiceNodeMessageHandler;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultPromise;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import org.springframework.util.CollectionUtils;

/**
 * @author: sucf
 * @date: 2024/1/3 23:25
 * @description: Gateway Pull Service Instance Node Tasker
 */
@Slf4j
public class GatewayPullServiceTimer implements Runnable {
    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    // 从注册中心拉取所有服务节点数据
                    DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new GatewayPullServiceMarkModel());
                    DefaultPromise<GatewayServiceNodeModel> promise = new DefaultPromise<>(CenterChannelManager.getChannel().eventLoop());
                    PullServiceNodeMessageHandler.promise = promise;
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
                        GatewayServiceNodeModel gatewayServiceNodeModel = promise.getNow();
                        Map<ProxyProviderModel, Set<ServerNodeModel>> map = gatewayServiceNodeModel.getServices();
                        ClientManager.reset(map);
                        GatewayServiceConfig.reset(gatewayServiceNodeModel.getConfig());
                        if(!CollectionUtils.isEmpty(map)) {
                            map.forEach((proxyProviderModel, proxyProviders) -> {
                                ClientManager.add(proxyProviderModel, proxyProviders);
                            });
                        }

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
