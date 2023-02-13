package com.junmo.center.web;

import com.google.common.collect.Lists;
import com.junmo.center.register.RegisterManager;
import com.junmo.center.web.vo.ProxyVO;
import com.junmo.core.ApiResult;
import com.junmo.core.model.ProviderModel;
import com.junmo.core.model.ServerNodeModel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description:
 */
@RestController
public class RegisterController {

    @RequestMapping(value = "/get/proxy")
    @ResponseBody
    public ApiResult<List<ProxyVO>> getProxy(String proxy, String provider) {
        List<ProxyVO> result = Lists.newArrayList();
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterManager.getServer();
        for (Map.Entry<String, Map<ProviderModel, Set<ServerNodeModel>>> entry : server.entrySet()) {
            if (StringUtils.hasLength(proxy) && !entry.getValue().equals(proxy)) {
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
                ProxyVO proxyVO = new ProxyVO();
                proxyVO.setProxy(proxy);
                proxyVO.setProvider(providerModel.getProvider());
                proxyVO.setVersion(providerModel.getVersion());
                proxyVO.setNumber(serverNodeModels.size());
                result.add(proxyVO);
            }
        }
        return ApiResult.buildSuccess(result);
    }

    @RequestMapping(value = "/get/server")
    @ResponseBody
    public ApiResult<Set<ServerNodeModel>> getServer(@RequestParam String proxy, @RequestParam String provider, @RequestParam(defaultValue = "0") Integer version) {
        Map<String, Map<ProviderModel, Set<ServerNodeModel>>> server = RegisterManager.getServer();
        Map<ProviderModel, Set<ServerNodeModel>> providerModels = server.get(proxy);
        Set<ServerNodeModel> serverNodeModels = providerModels.get(new ProviderModel(provider, version));
        return ApiResult.buildSuccess(serverNodeModels);
    }
}
