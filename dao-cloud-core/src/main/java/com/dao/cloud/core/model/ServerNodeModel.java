package com.dao.cloud.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ServerNodeModel implements Serializable {

    /**
     * ip
     */
    private String ip;

    /**
     * port
     */
    private int port;

    /**
     * on: true
     * off: false
     */
    private boolean status;

    /**
     * service load performance
     */
    private PerformanceModel performance;

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

    public ServerNodeModel(String ip, int port, PerformanceModel performance) {
        this.ip = ip;
        this.port = port;
        this.status = true;
        this.performance = performance;
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
