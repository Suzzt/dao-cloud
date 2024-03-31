package com.dao.cloud.center.core.model;

import com.dao.cloud.core.model.ProxyProviderModel;
import com.dao.cloud.core.model.ServerNodeModel;
import lombok.Data;

import java.util.Objects;

/**
 * @author: sucf
 * @date: 2024/3/29 23:57
 * @description:
 */
@Data
public class ServerProxyProviderNode {

    private ProxyProviderModel proxyProviderModel;

    private String ip;

    private Integer port;

    public ServerProxyProviderNode(ProxyProviderModel proxyProviderModel, String ip, Integer port) {
        this.proxyProviderModel = proxyProviderModel;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerProxyProviderNode that = (ServerProxyProviderNode) o;
        return Objects.equals(proxyProviderModel, that.proxyProviderModel) && Objects.equals(ip, that.ip) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxyProviderModel, ip, port);
    }
}
