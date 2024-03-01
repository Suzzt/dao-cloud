package com.dao.cloud.core.model;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/2/7 14:37
 */
@Data
public class HttpServletRequestModel implements Serializable {

    /**
     * http 请求方法
     */
    private String httpMethod;

    /**
     * 请求头
     */
    private Map<String, String> heads;

    /**
     * 请求参数，该参数包括 uri 上的和 multipart/form-data 类型的表单数据
     */
    private Map<String, String[]> params;

    /**
     * 请求体数据
     */
    private byte[] bodyData;

    /**
     * 请求参数
     * /api/{proxy}/{provider}/{version}/{method}
     */
    private String URI;
}
