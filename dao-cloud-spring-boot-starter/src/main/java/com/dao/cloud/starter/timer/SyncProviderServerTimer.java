package com.dao.cloud.starter.timer;

import com.dao.cloud.starter.manager.ClientManager;
import com.dao.cloud.starter.manager.RegistryManager;
import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import com.dao.cloud.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/1 17:14
 * 服务节点拉取定时器
 */
@Slf4j
public class SyncProviderServerTimer implements Runnable {

    private Set<ProxyProviderModel> relyProxy;

    public SyncProviderServerTimer(Set<ProxyProviderModel> relyProxy) {
        this.relyProxy = relyProxy;
    }

    @Override
    public void run() {
        for (ProxyProviderModel proxyProviderModel : relyProxy) {
            TimerTask task = new TimerTask() {
                @Override
                public void run(Timeout timeout) {
                    try {
                        Set<ServerNodeModel> serverNodeModels = RegistryManager.pull(proxyProviderModel);
                        ClientManager.save(proxyProviderModel, serverNodeModels);
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<< pull proxy = {}, provider = {} server node error >>>>>>>>>>>", proxyProviderModel.getProxy(), proxyProviderModel.getProviderModel(), e);
                    } finally {
                        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 3, TimeUnit.SECONDS);
                    }
                }
            };
            DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 3, TimeUnit.SECONDS);
        }
    }
}
