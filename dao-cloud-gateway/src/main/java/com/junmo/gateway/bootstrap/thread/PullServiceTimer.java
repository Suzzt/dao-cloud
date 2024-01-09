package com.junmo.gateway.bootstrap.thread;

import com.junmo.core.model.GatewayPullServiceMarkModel;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.util.DaoCloudConstant;
import com.junmo.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
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
