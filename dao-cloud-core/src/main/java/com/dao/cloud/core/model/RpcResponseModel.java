package com.dao.cloud.core.model;

import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import lombok.Data;

/**
 * @author sucf
 * @since 1.0
 * rpc 返回模型封装
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
