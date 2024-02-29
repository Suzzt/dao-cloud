package com.dao.cloud.starter.bootstrap.thread;

import com.dao.cloud.starter.bootstrap.manager.CenterChannelManager;
import com.dao.cloud.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/6/7 23:37
 * @description: 定时拉取集群节点信息
 */
@Slf4j
public class InquireClusterTimer implements Runnable {
    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    CenterChannelManager.inquire();
                } catch (Exception e) {
                    log.error("inquire cluster node error", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 5, TimeUnit.SECONDS);
    }
}
