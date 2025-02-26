package com.dao.cloud.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class CallTrendFullModel extends ErrorResponseModel{
    private List<CallTrendModel> callTrendModels;
}
