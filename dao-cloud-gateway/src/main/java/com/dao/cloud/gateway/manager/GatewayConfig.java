package com.dao.cloud.gateway.manager;

import com.dao.cloud.gateway.auth.Interceptor;
import com.dao.cloud.gateway.limit.Limiter;

import java.util.List;

/**
 * @author: sucf
 * @date: 2024/3/2 20:11
 * @description:
 */
public class GatewayConfig {
    private Limiter limiter;
    private List<Interceptor> interceptors;
    private Long timeout;
}
