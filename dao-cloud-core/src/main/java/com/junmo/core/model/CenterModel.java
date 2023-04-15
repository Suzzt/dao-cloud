package com.junmo.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2023/3/12 22:22
 * @description:
 */
@Data
public class CenterModel extends Model {
    private String cluster;

    private ServerNodeModel serverNodeModel;
}
