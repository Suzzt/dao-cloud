package com.junmo.core.model;

import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.DaoException;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/10/28 21:51
 * @description: rpc 返回模型封装
 */
@Data
public class RpcResponseModel extends ErrorResponseModel {

    public static RpcResponseModel builder(long sequenceId, CodeEnum codeEnum) {
        RpcResponseModel responseModel = new RpcResponseModel();
        responseModel.setSequenceId(sequenceId);
        responseModel.setDaoException(new DaoException(codeEnum));
        return responseModel;
    }

    private long sequenceId;

    /**
     * 返回值
     */
    private Object returnValue;
}
