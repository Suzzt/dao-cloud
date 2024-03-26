package com.dao.cloud.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: sucf
 * @date: 2023/1/9 21:33
 * @description:
 */
@Data
public class ServerNodeModel implements Serializable {
    private String ip;
    private int port;

    /**
     * on: true
     * false: off
     */
    private boolean status;

    public ServerNodeModel(String link) {
        String[] split = link.split(":");
        this.ip = split[0];
        this.port = Integer.parseInt(split[1]);
    }

    public ServerNodeModel(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.status = true;
    }

    public ServerNodeModel(String ip, int port, boolean status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerNodeModel that = (ServerNodeModel) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
