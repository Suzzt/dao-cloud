package com.junmo.config.register;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/11/28 11:12
 * @description:
 */
@Data
public class RegisterNode {

    private String remoteIpAddress;

    private int remotePort;

    private Boolean alive;

    public RegisterNode(String remoteIpAddress, int remotePort, Boolean alive) {
        this.remoteIpAddress = remoteIpAddress;
        this.remotePort = remotePort;
        this.alive = alive;
    }

    public RegisterNode() {
    }
}
