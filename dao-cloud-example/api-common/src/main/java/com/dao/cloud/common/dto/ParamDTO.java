package com.dao.cloud.common.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author sucf
 * @since 1.0
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
