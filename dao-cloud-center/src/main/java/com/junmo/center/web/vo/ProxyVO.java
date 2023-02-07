package com.junmo.center.web.vo;

import com.junmo.core.model.ServerNodeModel;
import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2023/2/7 20:43
 * @description:
 */
@Data
public class ProxyVO {
    private String proxy;

    private Integer version;

    private List<ServerNodeModel> servers;
}
