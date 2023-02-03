package com.junmo.core.model;

import com.junmo.core.exception.DaoException;
import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2023/1/13 23:41
 * @description:
 */
@Data
public class RegisterServerModel extends ResponseModel {
    private String proxy;

    private int version;

    private List<ServerNodeModel> serverNodeModes;

    public RegisterServerModel(String proxy, int version, List<ServerNodeModel> serverNodeModes) {
        this.proxy = proxy;
        this.version = version;
        this.serverNodeModes = serverNodeModes;
    }

    public RegisterServerModel(DaoException daoException) {
        this.exceptionValue = daoException;
    }
}
