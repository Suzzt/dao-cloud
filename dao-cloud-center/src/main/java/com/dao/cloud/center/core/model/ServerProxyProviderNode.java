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

    private ServerNodeModel serverNodeModel;

    public ServerProxyProviderNode(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        this.proxyProviderModel = proxyProviderModel;
        this.serverNodeModel = serverNodeModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerProxyProviderNode that = (ServerProxyProviderNode) o;
        return Objects.equals(proxyProviderModel, that.proxyProviderModel) && Objects.equals(serverNodeModel, that.serverNodeModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxyProviderModel, serverNodeModel);
    }
}
