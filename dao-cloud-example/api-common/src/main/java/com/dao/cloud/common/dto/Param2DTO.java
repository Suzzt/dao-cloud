package com.dao.cloud.common.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author sucf
 * @since 1.0
 */
@Data
public class Param2DTO implements Serializable {
    private String string2;
    private Integer integer2;
    private int intValue2;
    private double doubleValue2;
    private Long longValue2;
    private Boolean booleanValue2;
    private char charValue2;
    private byte byteValue2;
}
