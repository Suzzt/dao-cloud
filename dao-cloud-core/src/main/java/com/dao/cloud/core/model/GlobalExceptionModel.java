package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/20 22:58
 * @description:
 */
@Data
public class GlobalExceptionModel extends ErrorResponseModel {

    /**
     * request message type
     */
    private byte messageType;

}