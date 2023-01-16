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
    public RegisterPollModel(String proxy) {
        this.proxy = proxy;
    }
}
