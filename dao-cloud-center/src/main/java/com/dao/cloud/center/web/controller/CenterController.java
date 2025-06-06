package com.dao.cloud.center.web.controller;

import com.dao.cloud.center.core.*;
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
import org.yaml.snakeyaml.Yaml;

import javax.validation.Valid;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/11/19 18:16
 * 业务web接口
 */
@Controller
@RequestMapping(value = "dao-cloud")
public class CenterController {

    private final ConfigCenterManager configCenterManager;

    private final GatewayCenterManager gatewayCenterManager;

    private final RegisterCenterManager registerCenterManager;

    private final LogManager logManager;

    private final ConfigurationCenterManager configurationCenterManager;

    public CenterController(ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager, RegisterCenterManager registerCenterManager, LogManager logManager, ConfigurationCenterManager configurationCenterManager) {
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
        this.registerCenterManager = registerCenterManager;
        this.logManager = logManager;
        this.configurationCenterManager = configurationCenterManager;
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
    public ApiResult<Void> manage(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version, @RequestParam String ip, @RequestParam Integer port, @RequestParam Boolean status) {
        ServerNodeModel serverNodeModel = new ServerNodeModel(ip, port, status);
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, version);
        registerCenterManager.manage(proxyProviderModel, serverNodeModel);
        CenterClusterManager.syncServerConfigToCluster(SyncClusterInformationRequestHandler.SERVER_STATUS, proxyProviderModel, serverNodeModel);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/gateway/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<Void> save(@Valid @RequestBody GatewayVO gatewayVO) {
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
    public ApiResult delete(ProxyConfigModel proxyConfigModel) {
        configCenterManager.delete(proxyConfigModel);
        CenterClusterManager.syncConfigToCluster(SyncClusterInformationRequestHandler.DELETE_CONFIG, proxyConfigModel, null);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/pageList")
    @ResponseBody
    public ConfigDataVO getConfigProxy(String proxy, String key, Integer version, @RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length) {
        ConfigDataVO configDataVO = new ConfigDataVO();
        List<ConfigVO> list = configCenterManager.getConfigVO(proxy, key, version);
        // 分页
        int endIndex = Math.min(start + length, list.size());
        List<ConfigVO> data = list.subList(start, endIndex);
        configDataVO.setRecordsTotal(list.size());
        configDataVO.setRecordsFiltered(list.size());
        configDataVO.setData(data);
        return configDataVO;
    }

    /**
     * 获取配置文件列表
     *
     * @param proxy
     * @param proxy
     * @param groupId
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/configuration/pageList")
    @ResponseBody
    public ConfigurationVO pageList(
            @RequestParam String proxy,
            @RequestParam String groupId,
            @RequestParam String fileName,
            @RequestParam int start,
            @RequestParam int length) {

        List<ConfigurationFileInformationModel> allData = configurationCenterManager.getConfiguration(proxy, groupId, fileName);

        // 分页计算
        int fromIndex = Math.min(start, allData.size());
        int toIndex = Math.min(start + length, allData.size());

        ConfigurationVO vo = new ConfigurationVO();
        vo.setData(allData.subList(fromIndex, toIndex));
        vo.setRecordsTotal(allData.size());
        vo.setRecordsFiltered(allData.size());

        return vo;
    }

    /**
     * 获取配置文件内容
     *
     * @param proxy
     * @param groupId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/configuration/property")
    @ResponseBody
    public ApiResult<String> getConfigurationProperty(@RequestParam String proxy, @RequestParam String groupId, @RequestParam String fileName) {
        return ApiResult.buildSuccess(configurationCenterManager.getConfigurationProperty(proxy, groupId, fileName));
    }

    /**
     * 删除配置文件
     *
     * @param proxy
     * @param groupId
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/configuration/delete")
    @ResponseBody
    public ApiResult<Void> delete(@RequestParam String proxy, @RequestParam String groupId, @RequestParam String fileName) {
        configurationCenterManager.delete(proxy, groupId, fileName);
        CenterClusterManager.syncConfigurationToCluster(SyncClusterInformationRequestHandler.DELETE_CONFIGURATION, proxy, groupId, fileName, null);
        return ApiResult.buildSuccess();
    }

    /**
     * 保存配置文件
     *
     * @param proxy
     * @param groupId
     * @param fileName
     * @param content
     * @return
     */
    @RequestMapping(value = "/configuration/save")
    @ResponseBody
    public ApiResult<String> save(@RequestParam String proxy, @RequestParam String groupId, @RequestParam String fileName, @RequestParam String content) {
        // 验证内容格式
        try {
            if (fileName.endsWith(".yaml")) {
                Object yamlContent = new Yaml().load(content);
                if (!(yamlContent instanceof Map)) {
                    throw new Exception("YAML内容必须为键值对结构");
                }
            } else {
                Properties props = new Properties();
                props.load(new StringReader(content));
                if (props.isEmpty()) {
                    throw new Exception("Properties内容不能为空");
                }
                if (!content.matches("(?m)^.*[=:].*$")) {
                    throw new Exception("Properties内容缺少键值分隔符");
                }
            }
        } catch (Exception e) {
            return ApiResult.buildFail("A0001", "配置文件格式错误: " + e.getMessage());
        }
        configurationCenterManager.save(proxy, groupId, fileName, content);
        CenterClusterManager.syncConfigurationToCluster(SyncClusterInformationRequestHandler.SAVE_CONFIGURATION, proxy, groupId, fileName, content);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/call_trend/statistics", method = RequestMethod.GET)
    @ResponseBody
    public List<CallTrendVO> trends(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        return registerCenterManager.getCallTrend(new ProxyProviderModel(proxy, provider, version));
    }

    @RequestMapping(value = "/call_trend/clear", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult clear(@Valid @RequestBody CallTrendClearVO callTrendClearVO) {
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(callTrendClearVO.getProxy(), callTrendClearVO.getProvider(), callTrendClearVO.getVersion());
        registerCenterManager.callTrendClear(proxyProviderModel, callTrendClearVO.getMethodName());
        CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, callTrendClearVO.getMethodName(), null);
        CenterClusterManager.syncCallTrendToCluster(SyncClusterInformationRequestHandler.CALL_TREND_CLEAR, callTrendModel);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/log/search", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult<List<LogVO>> search(@RequestParam String traceId) {
        try {
            List<LogModel> logModels = logManager.get(traceId);
            List<LogVO> result = Lists.newArrayList();
            for (LogModel logModel : logModels) {
                LogVO logVO = new LogVO();
                logVO.setLog(logModel.getLogMessage());
                logVO.setNode(logModel.getNode());
                result.add(logVO);
            }
            return ApiResult.buildSuccess(result);
        } catch (Exception e) {
            return ApiResult.buildSuccess();
        }
    }
}
