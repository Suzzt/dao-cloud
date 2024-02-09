package com.junmo.center.web.controller;

import com.google.common.collect.Lists;
import com.junmo.center.core.CenterClusterManager;
import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.GatewayCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.center.web.interceptor.Permissions;
import com.junmo.center.web.vo.*;
import com.junmo.core.ApiResult;
import com.junmo.core.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description: 业务web接口
 */
@Controller
@RequestMapping(value = "dao-cloud")
public class CenterController {

    private ConfigCenterManager configCenterManager;

    private GatewayCenterManager gatewayCenterManager;

    public CenterController(ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @RequestMapping(value = "/registry/pageList")
    @ResponseBody
    @Permissions(limit = false)
    public ApiResult<List<ServerVO>> getRegisterProxy(String proxy, String provider) {
        List<ServerVO> result = Lists.newArrayList();
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterCenterManager.getServer();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : server.entrySet()) {
            if (StringUtils.hasLength(proxy) && !entry.getKey().equals(proxy)) {
                continue;
            }
            String proxyKey = entry.getKey();
            Map<ProviderModel, Set<ServerNodeModel>> providerModels = entry.getValue();
            for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : providerModels.entrySet()) {
                ProviderModel providerModel = providerModelSetEntry.getKey();
                if (StringUtils.hasLength(provider) && !providerModel.getProvider().equals(provider)) {
                    continue;
                }
                Set<ServerNodeModel> serverNodeModels = providerModelSetEntry.getValue();
                ServerVO serverVO = new ServerVO();
                serverVO.setProxy(proxyKey);
                serverVO.setProvider(providerModel.getProvider());
                serverVO.setVersion(providerModel.getVersion());
                serverVO.setNumber(serverNodeModels.size());

                // get limiter
                LimitModel limiter = gatewayCenterManager.getLimiter(new ProxyProviderModel(proxyKey, providerModel));
                serverVO.setLimit(limiter);
                result.add(serverVO);
            }
        }
        return ApiResult.buildSuccess(result);
    }

    @RequestMapping(value = "/registry/server")
    @ResponseBody
    public ApiResult<Set<ServerNodeModel>> getServer(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterCenterManager.getServer();
        Map<ProviderModel, Set<ServerNodeModel>> providerModels = server.get(proxy);
        Set<ServerNodeModel> serverNodeModels = providerModels.get(new ProviderModel(provider, version));
        return ApiResult.buildSuccess(serverNodeModels);
    }

    @RequestMapping(value = "/gateway/limit_save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult save(@Valid GatewayLimitVO gatewayLimitVO) {
        gatewayCenterManager.save(gatewayLimitVO);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/gateway/limit_clear", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult clear(@Valid ServiceBaseVO serviceBaseVO) {
        gatewayCenterManager.clear(serviceBaseVO);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult save(@Valid ConfigVO configVO) {
        ProxyConfigModel proxyConfigModel = new ProxyConfigModel();
        proxyConfigModel.setProxy(configVO.getProxy());
        proxyConfigModel.setKey(configVO.getKey());
        proxyConfigModel.setVersion(configVO.getVersion());
        configCenterManager.save(proxyConfigModel, configVO.getContent());
        CenterClusterManager.syncConfigToCluster((byte) 2, proxyConfigModel, configVO.getContent());
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<List<ConfigVO>> delete(ProxyConfigModel proxyConfigModel) {
        configCenterManager.delete(proxyConfigModel);
        CenterClusterManager.syncConfigToCluster((byte) -2, proxyConfigModel, null);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/pageList")
    @ResponseBody
    public ConfigDataVO getConfigProxy(String proxy, String key, @RequestParam(required = false, defaultValue = "0") int start,
                                       @RequestParam(required = false, defaultValue = "10") int length) {
        ConfigDataVO configDataVO = new ConfigDataVO();
        List<ConfigVO> list = configCenterManager.getConfigVO(proxy, key);
        // 分页
        int endIndex = Math.min(start + length, list.size());
        List<ConfigVO> data = list.subList(start, endIndex);
        configDataVO.setRecordsTotal(list.size());
        configDataVO.setRecordsFiltered(list.size());
        configDataVO.setData(data);
        return configDataVO;
    }
}
