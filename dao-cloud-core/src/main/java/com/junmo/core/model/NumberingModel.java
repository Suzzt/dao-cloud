package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/4/22 17:32
 * @description:
 */
@Data
public class NumberingModel extends Model {
    private long sequenceId;

    private String errorMessage;
}
