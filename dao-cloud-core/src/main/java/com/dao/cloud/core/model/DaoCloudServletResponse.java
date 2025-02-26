package com.dao.cloud.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author wuzhenhong
 * @since 1.0
 */
@Data
public class DaoCloudServletResponse implements Serializable {

    /**
     * 响应头
     */
    private Map<String, String> heads;
    /**
     * 返回数据体
     */
    private byte[] bodyData;

    public DaoCloudServletResponse() {
        this.heads = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        this.heads.put(name, value);
    }
}
