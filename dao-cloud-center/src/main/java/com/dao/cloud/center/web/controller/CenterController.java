package com.dao.cloud.center.web.controller;

import com.dao.cloud.center.core.CenterClusterManager;
import com.dao.cloud.center.core.ConfigCenterManager;
import com.dao.cloud.center.core.GatewayCenterManager;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.center.core.handler.SyncClusterInformationRequestHandler;
import com.dao.cloud.center.core.model.ServiceNode;
import com.dao.cloud.center.web.interceptor.Permissions;
import com.dao.cloud.center.web.vo.*;
import com.dao.cloud.core.ApiResult;
import com.dao.cloud.core.model.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    private RegisterCenterManager registerCenterManager;

    public CenterController(RegisterCenterManager registerCenterManager, ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        this.registerCenterManager = registerCenterManager;
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @RequestMapping(value = "/registry/pageList")
    @ResponseBody
    @Permissions(limit = false)
    public ApiResult<List<ServerVO>> getRegisterProxy(String proxy, String provider) {
        List<ServerVO> result = Lists.newArrayList();
        Map<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> server = registerCenterManager.getServer();
        for (Map.Entry<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> entry : server.entrySet()) {
            if (StringUtils.hasLength(proxy) && !entry.getKey().equals(proxy)) {
                continue;
            }
            String proxyKey = entry.getKey();
            Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModels = entry.getValue();
            for (Map.Entry<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModelSetEntry : providerModels.entrySet()) {
                ProviderModel providerModel = providerModelSetEntry.getKey();
                if (StringUtils.hasLength(provider) && !providerModel.getProvider().equals(provider)) {
                    continue;
                }
                Map<ServiceNode, ServerNodeModel> serverNodeModels = providerModelSetEntry.getValue();
                ServerVO serverVO = new ServerVO();
                serverVO.setProxy(proxyKey);
                serverVO.setProvider(providerModel.getProvider());
                serverVO.setVersion(providerModel.getVersion());
                serverVO.setNumber(serverNodeModels.size());

                // get gateway config
                GatewayConfigModel gateway = gatewayCenterManager.getGatewayConfig(new ProxyProviderModel(proxyKey, providerModel));
                serverVO.setGateway(gateway);
                result.add(serverVO);
            }
        }
        return ApiResult.buildSuccess(result);
    }

    @RequestMapping(value = "/registry/server")
    @ResponseBody
    public ApiResult<Set<ServerNodeModel>> getServer(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        Set<ServerNodeModel> set = Sets.newHashSet();
        Map<String, Map<ProviderModel, Map<ServiceNode, ServerNodeModel>>> server = registerCenterManager.getServer();
        Map<ProviderModel, Map<ServiceNode, ServerNodeModel>> providerModels = server.get(proxy);
        Map<ServiceNode, ServerNodeModel> serverNodeModels = providerModels.get(new ProviderModel(provider, version));
        for (Map.Entry<ServiceNode, ServerNodeModel> serverNodeModelEntry : serverNodeModels.entrySet()) {
            set.add(serverNodeModelEntry.getValue());
        }
        return ApiResult.buildSuccess(set);
    }

    @RequestMapping(value = "/registry/on_off")
    @ResponseBody
    public ApiResult manage(@RequestParam String proxy, @RequestParam String provider,
                            @RequestParam(defaultValue = "0") Integer version,
                            @RequestParam String ip, @RequestParam Integer port,
                            @RequestParam Boolean status) {
        ServerNodeModel serverNodeModel = new ServerNodeModel(ip, port, status);
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
        registerCenterManager.manage(proxyProviderModel, serverNodeModel);
        CenterClusterManager.syncServerConfigToCluster(SyncClusterInformationRequestHandler.SAVE_SERVER, proxyProviderModel, serverNodeModel);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/gateway/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult save(@Valid @RequestBody GatewayVO gatewayVO) {
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(gatewayVO.getProxy(), gatewayVO.getProvider(), gatewayVO.getVersion());
        GatewayConfigModel gatewayConfigModel = new GatewayConfigModel();
        gatewayConfigModel.setLimitModel(gatewayVO.getLimit());
        gatewayConfigModel.setTimeout(gatewayVO.getTimeout());
        gatewayCenterManager.save(proxyProviderModel, gatewayConfigModel);
        CenterClusterManager.syncGatewayConfigToCluster(SyncClusterInformationRequestHandler.SAVE_GATEWAY, proxyProviderModel, gatewayConfigModel);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/gateway/clear", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult clear(@Valid GatewayVO gatewayVO) {
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(gatewayVO.getProxy(), gatewayVO.getProvider(), gatewayVO.getVersion());
        gatewayCenterManager.clear(proxyProviderModel);
        CenterClusterManager.syncGatewayConfigToCluster(SyncClusterInformationRequestHandler.DELETE_GATEWAY, proxyProviderModel, null);
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
        CenterClusterManager.syncConfigToCluster(SyncClusterInformationRequestHandler.SAVE_CONFIG, proxyConfigModel, configVO.getContent());
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<List<ConfigVO>> delete(ProxyConfigModel proxyConfigModel) {
        configCenterManager.delete(proxyConfigModel);
        CenterClusterManager.syncConfigToCluster(SyncClusterInformationRequestHandler.DELETE_CONFIG, proxyConfigModel, null);
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

    @RequestMapping(value = "/call_trend/statistics", method = RequestMethod.GET)
    @ResponseBody
    public List<CallTrendVO> trends(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        return registerCenterManager.getCallTrend(new ProxyProviderModel(proxy, provider, version));
    }
}
