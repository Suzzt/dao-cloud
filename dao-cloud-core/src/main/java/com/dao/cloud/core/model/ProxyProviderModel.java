package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author: sucf
 * @date: 2023/1/14 16:48
 * @description:
 */
@Data
public class ProxyProviderModel extends Model {
    private String proxy;

    private ProviderModel providerModel;

    public ProxyProviderModel(String proxy, ProviderModel providerModel) {
        this.proxy = proxy;
        this.providerModel = providerModel;
    }

    public ProxyProviderModel(String proxy, String provider, int version) {
        this.proxy = proxy;
        this.providerModel = new ProviderModel(provider, version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProxyProviderModel that = (ProxyProviderModel) o;
        return Objects.equals(proxy, that.proxy) && Objects.equals(providerModel, that.providerModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proxy, providerModel);
    }
}
