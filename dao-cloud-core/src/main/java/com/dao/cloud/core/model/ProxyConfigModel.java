package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ProxyConfigModel extends Model {
    private String proxy;

    private String key;

    private int version;

    public ProxyConfigModel(String proxy, String key, int version) {
        this.proxy = proxy;
        this.key = key;
        this.version = version;
    }

    public ProxyConfigModel() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProxyConfigModel that = (ProxyConfigModel) o;
        return version == that.version && Objects.equals(proxy, that.proxy) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proxy, key, version);
    }

    @Override
    public String toString() {
        return this.proxy + "|" + this.key + "|" + this.version;
    }
}
