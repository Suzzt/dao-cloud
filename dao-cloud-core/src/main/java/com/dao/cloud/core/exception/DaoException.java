package com.dao.cloud.core.exception;

import com.dao.cloud.core.enums.CodeEnum;

import java.io.Serializable;

/**
 * @author sucf
 * @since 1.0
 */
public class DaoException extends RuntimeException implements Serializable {

    /**
     * 错误码
     *
     * @see CodeEnum
     */
    public String code;

    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(Throwable t) {
        super(t);
    }

    public DaoException(String msg, Throwable t) {
        super(msg, t);
    }

    public DaoException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public DaoException(CodeEnum errorInfo) {
        this(errorInfo.getCode(), errorInfo.getText());
    }

    public String getCode() {
        return code;
    }
}
