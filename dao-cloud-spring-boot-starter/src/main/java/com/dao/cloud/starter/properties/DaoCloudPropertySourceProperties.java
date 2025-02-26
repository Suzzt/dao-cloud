package com.dao.cloud.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sucf
 * @since 1.0
 * 配置属性加载
 */
@ConfigurationProperties("dao.cloud.config")
public class DaoCloudPropertySourceProperties {
    private boolean overrideSystemProperties = true;
    private boolean allowOverride = true;
    private boolean overrideNone = false;

    public boolean isOverrideSystemProperties() {
        return overrideSystemProperties;
    }

    public void setOverrideSystemProperties(boolean overrideSystemProperties) {
        this.overrideSystemProperties = overrideSystemProperties;
    }

    public boolean isAllowOverride() {
        return allowOverride;
    }

    public void setAllowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    public boolean isOverrideNone() {
        return overrideNone;
    }

    public void setOverrideNone(boolean overrideNone) {
        this.overrideNone = overrideNone;
    }
}
