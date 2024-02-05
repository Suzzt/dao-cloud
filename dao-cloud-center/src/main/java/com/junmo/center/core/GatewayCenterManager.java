package com.junmo.center.core;

import com.junmo.core.model.LimitModel;
import com.junmo.core.model.ProxyProviderModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * @author: sucf
 * @date: 2024/2/5 13:59
 * @description: 网关中心
 */
@Slf4j
public class GatewayCenterManager {

    /**
     * 限流设置
     */
    private Map<ProxyProviderModel, LimitModel> cache;

    public void init() {

    }
}
