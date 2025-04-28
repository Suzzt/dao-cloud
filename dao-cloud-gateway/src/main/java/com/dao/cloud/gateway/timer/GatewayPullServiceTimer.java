package com.dao.cloud.gateway.timer;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.DaoTimer;
import com.dao.cloud.gateway.limit.LimitFactory;
import com.dao.cloud.gateway.limit.Limiter;
import com.dao.cloud.gateway.model.GatewayConfig;
import com.dao.cloud.gateway.manager.GatewayConfigManager;
import com.dao.cloud.starter.manager.CenterChannelManager;
import com.dao.cloud.starter.manager.ClientManager;
import com.dao.cloud.starter.handler.GatewayPullServiceNodeMessageHandler;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/1/3 23:25
 * Gateway Pull Service Instance Node Tasker
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
                    DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.GATEWAY_REGISTER_ALL_SERVER_REQUEST_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, new GatewayConfigPullMarkModel());
                    DefaultPromise<GatewayServiceNodeModel> promise = new DefaultPromise<>(CenterChannelManager.getChannel().eventLoop());
                    GatewayPullServiceNodeMessageHandler.promise = promise;
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
                        Map<ProxyProviderModel, GatewayConfigModel> config = gatewayServiceNodeModel.getConfig();
                        Map<ProxyProviderModel, Set<ServerNodeModel>> services = gatewayServiceNodeModel.getServices();
                        for (Map.Entry<ProxyProviderModel, Set<ServerNodeModel>> entry : services.entrySet()) {
                            ProxyProviderModel proxyProviderModel = entry.getKey();
                            Set<ServerNodeModel> serverNodeModels = entry.getValue();
                            ClientManager.save(proxyProviderModel, serverNodeModels);
                    
                            // gateway config
                            GatewayConfigModel gatewayConfigModel = config.get(proxyProviderModel);
                            if (gatewayConfigModel == null) {
                                GatewayConfigManager.save(proxyProviderModel, null);
                                continue;
                            }
                            GatewayConfig gatewayConfig = new GatewayConfig();
                            Limiter limiter = LimitFactory.getLimiter(gatewayConfigModel.getLimitModel());
                            gatewayConfig.setLimiter(limiter);
                            gatewayConfig.setTimeout(gatewayConfigModel.getTimeout());
                            GatewayConfigManager.save(proxyProviderModel, gatewayConfig);
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
