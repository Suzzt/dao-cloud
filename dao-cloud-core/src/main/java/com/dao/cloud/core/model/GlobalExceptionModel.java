package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class GlobalExceptionModel extends ErrorResponseModel {

    /**
     * request message type
     */
    private byte messageType;

}