package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/1 20:28
 * @description:
 */
@Data
public class ResponseModel extends Model {
    /**
     * error message
     */
    public String errorMessage;
}
