package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/7/15 23:31
 */
@Data
public class ClusterSyncDataResponseModel extends ErrorResponseModel {
    private Long sequenceId;
}
