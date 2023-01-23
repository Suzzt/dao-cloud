package com.junmo.core.exception;

/**
 * @author: sucf
 * @date: 2023/1/9 10:38
 * @description:
 */
public class DaoException extends RuntimeException {
    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(Throwable t) {
        super(t);
    }
}