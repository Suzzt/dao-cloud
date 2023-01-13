package com.junmo.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: sucf
 * @date: 2022/11/13 23:19
 * @description:
 */
@Data
public class ServerRegisterModel implements Serializable {
    /**
     * proxy name (unique)
     */
    private String proxy;

    /**
     * ip address + port
     */
    private String ipLinkPort;

}
