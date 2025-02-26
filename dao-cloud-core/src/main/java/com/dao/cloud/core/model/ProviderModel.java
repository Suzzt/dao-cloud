package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ProviderModel extends Model {
    private String provider;

    private int version;

    public ProviderModel() {
    }

    public ProviderModel(String provider, int version) {
        this.provider = provider;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProviderModel that = (ProviderModel) o;
        return version == that.version && Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), provider, version);
    }

    @Override
    public String toString() {
        return "["+provider + "/" + version+"]";
    }
}
