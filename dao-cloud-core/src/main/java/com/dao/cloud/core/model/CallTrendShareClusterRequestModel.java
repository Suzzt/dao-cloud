package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/7/19 15:54
 * @description: Synchronization of call trend information between clusters
 */
@Data
public class CallTrendShareClusterRequestModel extends AbstractShareClusterRequestModel {
    private CallTrendModel callTrendModel;
}
