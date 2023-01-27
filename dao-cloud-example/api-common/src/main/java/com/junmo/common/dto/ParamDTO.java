package com.junmo.common.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author: sucf
 * @date: 2023/1/24 18:28
 * @description:
 */
@Data
public class ParamDTO implements Serializable {
    private String string;
    private Integer integer;
    private int intValue;
    private double doubleValue;
    private Long longValue;
    private Boolean booleanValue;
    private char charValue;
    private byte byteValue;
}
