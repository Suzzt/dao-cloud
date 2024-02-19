package com.junmo.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/2/7 14:37
 */
@Data
public class HttpServletResponse implements Serializable {

    /**
     * 响应头
     */
    private Map<String, String> heads;
    /**
     * 返回数据体
     */
    private byte[] bodyData;

    public HttpServletResponse() {
        this.heads = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        this.heads.put(name, value);
    }
}
