package com.junmo.center.web;

import com.google.common.collect.Lists;
import com.junmo.center.register.RegisterClient;
import com.junmo.center.web.vo.ProxyVO;
import com.junmo.core.ApiResult;
import com.junmo.core.model.RegisterProxyModel;
import com.junmo.core.model.ServerNodeModel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description:
 */
@RestController
public class RegisterController {

    @RequestMapping(value = "/get/proxy")
    @ResponseBody
    public ApiResult<List<ProxyVO>> getProxy(String proxy, Integer version) {
        List<ProxyVO> result = Lists.newArrayList();
        Map<String, Map<String, String>> serverMap = RegisterClient.SERVER_MAP;
        for (Map.Entry<String, Map<String, String>> entry : serverMap.entrySet()) {
            RegisterProxyModel registerProxyModel = RegisterClient.parseKey(entry.getKey());
            if (StringUtils.hasLength(proxy) && !registerProxyModel.getProxy().equals(proxy)) {
                continue;
            }
            if (version != null && registerProxyModel.getVersion() != version) {
                continue;
            }
            ProxyVO proxyVO = new ProxyVO();
            proxyVO.setProxy(registerProxyModel.getProxy());
            proxyVO.setVersion(registerProxyModel.getVersion());
            proxyVO.setNumber(entry.getValue().size());
            result.add(proxyVO);
        }
        return ApiResult.buildSuccess(result);
    }

    @RequestMapping(value = "/get/server")
    @ResponseBody
    public ApiResult<List<ServerNodeModel>> getServer(@RequestParam String proxy, @RequestParam(defaultValue = "0") Integer version) {
        List<ServerNodeModel> servers = Lists.newArrayList();
        Map<String, Map<String, String>> serverMap = RegisterClient.SERVER_MAP;
        Map<String, String> nodeMap = serverMap.get(proxy + "#" + version);
        if (!CollectionUtils.isEmpty(nodeMap)) {
            for (Map.Entry<String, String> nodeEntry : nodeMap.entrySet()) {
                String ipLinkPort = nodeEntry.getKey();
                servers.add(new ServerNodeModel(ipLinkPort));
            }
        }
        return ApiResult.buildSuccess(servers);
    }
}
