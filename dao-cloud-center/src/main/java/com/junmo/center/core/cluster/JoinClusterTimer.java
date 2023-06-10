package com.junmo.center.core.cluster;

import com.junmo.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/4/22 17:48
 * @description:
 */
@Slf4j
public class JoinClusterTimer implements Runnable {

    private ClusterCenterConnector clusterCenterConnector;

    public JoinClusterTimer(ClusterCenterConnector clusterCenterConnector) {
        this.clusterCenterConnector = clusterCenterConnector;
    }

    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    clusterCenterConnector.sendHeartbeat();
                } catch (Exception e) {
                    log.error("<<<<<<<<< join cluster error >>>>>>>>>", e);
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 5, TimeUnit.SECONDS);
    }
}
