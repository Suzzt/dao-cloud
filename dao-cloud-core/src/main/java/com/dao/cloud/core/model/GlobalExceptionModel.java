package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/2/20 22:58
 */
@Data
public class GlobalExceptionModel extends ErrorResponseModel {

    /**
     * request message type
     */
    private byte messageType;

}