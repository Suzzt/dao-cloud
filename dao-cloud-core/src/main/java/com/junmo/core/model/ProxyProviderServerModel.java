package com.junmo.core.model;

import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.DaoException;
import lombok.Data;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/1/13 23:41
 * @description:
 */
@Data
public class ProxyProviderServerModel extends ErrorResponseModel {

    private String proxy;

    private ProviderModel providerModel;

    private Set<ServerNodeModel> serverNodeModes;

    public ProxyProviderServerModel(String proxy, ProviderModel providerModel, Set<ServerNodeModel> serverNodeModes) {
        this.proxy = proxy;
        this.providerModel = providerModel;
        this.serverNodeModes = serverNodeModes;
    }

    public ProxyProviderServerModel(CodeEnum codeEnum) {
        setDaoException(new DaoException(codeEnum));
    }
}
