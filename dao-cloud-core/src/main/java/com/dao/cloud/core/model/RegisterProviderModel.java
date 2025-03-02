package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/11/13 23:19
 */
@Data
public class RegisterProviderModel extends Model {

    /**
     * proxy name (unique)
     */
    private String proxy;

    /**
     * provider server
     */
    private Set<ProviderModel> providerModels;

    /**
     * ip address + port
     */
    private ServerNodeModel serverNodeModel;

}
