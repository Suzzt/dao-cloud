package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/1/9 21:33
 * @description:
 */
@Data
public class ServerNodeModel {
    private String ip;
    private int port;

    public String link() {
        return this.ip + ":" + this.port;
    }

    public ServerNodeModel(String link) {
        String[] split = link.split(":");
        this.ip = split[0];
        this.port = Integer.parseInt(split[1]);
    }
}
