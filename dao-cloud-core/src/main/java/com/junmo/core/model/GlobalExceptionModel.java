package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/20 22:58
 * @description:
 */
@Data
public class GlobalExceptionModel extends Model {

    /**
     * request message type
     */
    private byte messageType;

    private String errorCode;

    private String errorMessage;
}