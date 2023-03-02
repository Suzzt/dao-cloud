package com.junmo.center.web;

import com.google.common.collect.Lists;
import com.junmo.center.core.ConfigCenterManager;
import com.junmo.center.core.RegisterCenterManager;
import com.junmo.center.web.vo.ConfigVO;
import com.junmo.center.web.vo.ServerVO;
import com.junmo.core.ApiResult;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.model.ServerNodeModel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description:
 */
@RestController
@RequestMapping(value = "dao-cloud")
public class CenterController {

    @RequestMapping(value = "/register/proxy")
    @ResponseBody
    public ApiResult<List<ServerVO>> getRegisterProxy(String proxy, String provider) {
        List<ServerVO> result = Lists.newArrayList();
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterCenterManager.getServer();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : server.entrySet()) {
            if (StringUtils.hasLength(proxy) && !entry.getKey().equals(proxy)) {
                continue;
            }
            proxy = entry.getKey();
            Map<ProviderModel, Set<ServerNodeModel>> providerModels = entry.getValue();
            for (Map.Entry<ProviderModel, Set<ServerNodeModel>> providerModelSetEntry : providerModels.entrySet()) {
                ProviderModel providerModel = providerModelSetEntry.getKey();
                if (StringUtils.hasLength(provider) && !providerModel.getProvider().equals(provider)) {
                    continue;
                }
                Set<ServerNodeModel> serverNodeModels = providerModelSetEntry.getValue();
                ServerVO serverVO = new ServerVO();
                serverVO.setProxy(proxy);
                serverVO.setProvider(providerModel.getProvider());
                serverVO.setVersion(providerModel.getVersion());
                serverVO.setNumber(serverNodeModels.size());
                result.add(serverVO);
            }
        }
        return ApiResult.buildSuccess(result);
    }

    @RequestMapping(value = "/register/server")
    @ResponseBody
    public ApiResult<Set<ServerNodeModel>> getServer(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterCenterManager.getServer();
        Map<ProviderModel, Set<ServerNodeModel>> providerModels = server.get(proxy);
        Set<ServerNodeModel> serverNodeModels = providerModels.get(new ProviderModel(provider, version));
        return ApiResult.buildSuccess(serverNodeModels);
    }

    @RequestMapping(value = "/config/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult save(@Valid @RequestBody ConfigVO configVO) {
        ProxyConfigModel proxyConfigModel = new ProxyConfigModel();
        proxyConfigModel.setProxy(configVO.getProxy());
        proxyConfigModel.setKey(configVO.getKey());
        proxyConfigModel.setVersion(configVO.getVersion());
        ConfigCenterManager.update(proxyConfigModel, configVO.getValue());
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<List<ConfigVO>> delete(ProxyConfigModel proxyConfigModel) {
        ConfigCenterManager.delete(proxyConfigModel);
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "/config/query")
    @ResponseBody
    public ApiResult<List<ConfigVO>> getConfigProxy(String proxy, String key) {
        return ApiResult.buildSuccess(ConfigCenterManager.getConfigVO(proxy, key));
    }
}
