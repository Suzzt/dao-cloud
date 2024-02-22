package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/1 20:28
 * @description: 响应错误返回模型
 *
 */
@Data
public class ErrorResponseModel extends Model {
    // todo 这里最好是定义一个异常返回回去, client端根据异常类型来处理自己的问题, 而不是现在判断是否有errorCode

    /**
     * error code
     */
    private String errorCode;

    /**
     * error message
     */
    public String errorMessage;
}
