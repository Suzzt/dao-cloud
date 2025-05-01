package com.dao.cloud.center.core.cluster;

import com.dao.cloud.core.util.DaoTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * center集群负载均衡定时器
 *
 * @author sucf
 * @date 2025/5/1 20:23
 * 扫描center cluster连接数定时器.5min/scan
 * 用来检测当前连接到center服务节点数是否与其他center节点承载的节点数相差过大，过大的时候，则需要触发rebalance connector.
 * @since 1.0.0
 */
public class ClusterLoadTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClusterLoadTimer.class);

    @Override
    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                try {
                    // todo 拿取其他center节点服务负载情况
                    // 判断是否需要触发rebalance
                    ///触发rebalance
                } catch (Exception e) {
                } finally {
                    DaoTimer.HASHED_WHEEL_TIMER.newTimeout(this, 5, TimeUnit.SECONDS);
                }
            }
        };
        DaoTimer.HASHED_WHEEL_TIMER.newTimeout(task, 5, TimeUnit.SECONDS);
    }
}
