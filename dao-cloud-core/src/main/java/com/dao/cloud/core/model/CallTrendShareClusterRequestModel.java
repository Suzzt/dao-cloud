package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 *@since 1.0
 * Synchronization of call trend information between clusters
 */
@Data
public class CallTrendShareClusterRequestModel extends AbstractShareClusterRequestModel {
    private CallTrendModel callTrendModel;
}
