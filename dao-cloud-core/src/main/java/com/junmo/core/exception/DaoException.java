package com.junmo.core.exception;

/**
 * @author: sucf
 * @date: 2023/1/9 10:38
 * @description:
 */
public class DaoException extends RuntimeException {

    private String code;
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
}
