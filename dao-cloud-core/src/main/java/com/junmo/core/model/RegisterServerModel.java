package com.junmo.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2023/1/13 23:41
 * @description:
 */
@Data
public class RegisterServerModel extends Model {
    private String proxy;

    private List<ServerNodeModel> serverNodeModes;

    public RegisterServerModel(String proxy, List<ServerNodeModel> serverNodeModes) {
        this.proxy = proxy;
        this.serverNodeModes = serverNodeModes;
    }
}
