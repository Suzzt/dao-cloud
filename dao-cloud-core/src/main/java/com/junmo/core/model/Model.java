package com.junmo.core.model;

import com.junmo.core.exception.DaoException;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2023/1/13 17:15
 * @description:
 */
@Data
public class Model implements Serializable {
    /**
     * 异常值
     */
    private DaoException exceptionValue;
}
