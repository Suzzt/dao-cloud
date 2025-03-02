package com.dao.cloud.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/7/22 16:34
 */
@Data
public class CallTrendFullModel extends ErrorResponseModel{
    private List<CallTrendModel> callTrendModels;
}
