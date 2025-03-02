package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ConfigurationFileInformationModel extends Model {
    private String proxy;
    private String groupId;
    private String fileName;

    public ConfigurationFileInformationModel(String proxy, String groupId, String fileName) {
        this.proxy = proxy;
        this.groupId = groupId;
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConfigurationFileInformationModel that = (ConfigurationFileInformationModel) o;
        return Objects.equals(proxy, that.proxy) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proxy, groupId);
    }
}
