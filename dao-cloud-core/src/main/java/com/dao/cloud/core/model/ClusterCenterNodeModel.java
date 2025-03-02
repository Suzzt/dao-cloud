package com.dao.cloud.core.model;

import lombok.Data;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/3/12 22:22
 * cluster alive nodes
 */
@Data
public class ClusterCenterNodeModel extends Model {
    private Set<String> ClusterNodes;
}
