package com.junmo.center.web.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: sucf
 * @date: 2024/2/6 00:13
 * @description:
 */
@Data
public class ServiceBaseVO {

    @NotNull(message = "proxy不能为空")
    private String proxy;
    @NotNull(message = "key不能为空")
    private String key;
    @NotNull(message = "version不能为空")
    private Integer version;

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
