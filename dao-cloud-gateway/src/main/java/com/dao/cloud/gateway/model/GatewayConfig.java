package com.dao.cloud.gateway.model;

import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.limit.Limiter;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author: sucf
 * @date: 2024/3/2 20:11
 * @description:
 */
@Data
public class GatewayConfig {
    private Limiter limiter;
    private List<Interceptor> interceptors;
    private Long timeout;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatewayConfig that = (GatewayConfig) o;
        return Objects.equals(limiter, that.limiter) && Objects.equals(interceptors, that.interceptors) && Objects.equals(timeout, that.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limiter, interceptors, timeout);
    }
}
