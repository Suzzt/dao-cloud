package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/11/13 23:19
 * @description:
 */
@Data
public class RegisterModel extends Model {
    /**
     * proxy name (unique)
     */
    private String proxy;

    /**
     * ip address + port
     */
    private String ipLinkPort;

}
