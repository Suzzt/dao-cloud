package com.dao.cloud.core.util;

import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/2 22:03
 */
@Slf4j
public class DaoTimer {
    public static final HashedWheelTimer HASHED_WHEEL_TIMER = new HashedWheelTimer(
            new DefaultThreadFactory("dao-timer"),
            100,
            TimeUnit.MILLISECONDS,
            512,
            true);
}
