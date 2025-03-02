package com.dao.cloud.center.core.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/6/19 15:55
 * service node info
 */
@Data
public class ServiceNode {
    private String ip;
    private Integer port;

    public ServiceNode(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceNode that = (ServiceNode) o;
        return Objects.equals(ip, that.ip) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
