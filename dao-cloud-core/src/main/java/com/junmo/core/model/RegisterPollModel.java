package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/1/14 16:48
 * @description:
 */
@Data
public class RegisterPollModel extends Model{
    private String proxy;

    private int version;

    public RegisterPollModel(String proxy, int version) {
        this.proxy = proxy;
        this.version = version;
    }
}
