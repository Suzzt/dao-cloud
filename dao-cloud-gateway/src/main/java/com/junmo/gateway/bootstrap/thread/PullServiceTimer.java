package com.junmo.gateway.bootstrap.thread;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.junmo.boot.bootstrap.manager.ClientManager;
import com.junmo.boot.bootstrap.manager.RegistryManager;
import com.junmo.core.model.ProxyProviderModel;
import com.junmo.core.model.ServerNodeModel;
import com.junmo.core.util.DaoTimer;
import com.junmo.gateway.manager.GatewayServiceManager;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
                    // todo 拉取所有服务节点
                    GatewayServiceManager.reset(ClientManager.GetFullServiceNodes());
                } catch (Exception e) {
                    log.error("pull service node error", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 10, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 10, TimeUnit.SECONDS);
    }
}
