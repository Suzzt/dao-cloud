package com.dao.cloud.core.model;

import com.dao.cloud.core.exception.DaoException;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/2/1 20:28
 * @description: 响应错误返回模型
 *
 */
@Data
public class ErrorResponseModel extends Model {
    private DaoException daoException;
}
