package com.junmo.core.model;

import com.junmo.core.exception.DaoException;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/1 20:28
 * @description:
 */
@Data
public class ResponseModel extends Model{
    /**
     * 异常值
     */
    public DaoException exceptionValue;
}
