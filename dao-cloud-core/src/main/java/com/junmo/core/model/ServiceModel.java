package com.junmo.core.model;


import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/2/3 23:54
 * @description:
 */
public class ServiceModel extends Model {

    private String proxy;

    private ProviderModel providerModel;

    private LimitModel limitModel;

    public ServiceModel(String proxy, ProviderModel providerModel, LimitModel limitModel) {
        this.proxy = proxy;
        this.providerModel = providerModel;
        this.limitModel = limitModel;
    }

    @Data
    public static class LimitModel {
        public LimitModel(Integer limitAlgorithm, Integer limitNumber) {
            this.limitAlgorithm = limitAlgorithm;
            this.limitNumber = limitNumber;
        }

        /**
         * 限流算法
         */
        private Integer limitAlgorithm;

        /**
         * 限流数量
         */
        private Integer limitNumber;
    }
}
