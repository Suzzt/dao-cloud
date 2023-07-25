package com.junmo.core.model;

import lombok.Data;

import java.util.Set;

/**
 * @author: sucf
 * @date: 2023/3/12 22:22
 * @description: cluster alive nodes
 */
@Data
public class ClusterCenterNodeModel extends Model {
    private Set<String> ClusterNodes;
}
