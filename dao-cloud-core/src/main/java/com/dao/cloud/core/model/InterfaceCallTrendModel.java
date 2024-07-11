package com.dao.cloud.core.model;

import lombok.Data;


/**
 * @author: sucf
 * @date: 2024/7/11 16:01
 * @description: Synchronous interface call data
 */
@Data
public class InterfaceCallTrendModel extends Model {

    /**
     * Service information
     */
    private ProxyProviderModel proxyProviderModel;

    /**
     * Interface name
     */
    private String interfaceName;

    /**
     * Call count
     */
    private Long count;

}
