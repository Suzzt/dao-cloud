package com.junmo.config.web;

import com.junmo.config.register.ServerNode;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2022/11/19 18:19
 * @description:
 */
@Data
public class ServerInfoVO {
    private String proxyName;
    private ServerNode serverNode;
}
