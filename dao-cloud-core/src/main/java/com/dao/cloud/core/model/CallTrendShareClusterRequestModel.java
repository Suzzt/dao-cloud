package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/7/19 15:54
 * Synchronization of call trend information between clusters
 */
@Data
public class CallTrendShareClusterRequestModel extends AbstractShareClusterRequestModel {
    private CallTrendModel callTrendModel;
}
