package com.dao.cloud.core.model;

import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import lombok.Data;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0
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
