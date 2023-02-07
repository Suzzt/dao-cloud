package com.junmo.center.web;

import com.google.common.collect.Lists;
import com.junmo.center.register.RegisterClient;
import com.junmo.center.web.vo.ProxyVO;
import com.junmo.core.ApiResult;
import com.junmo.core.model.RegisterProxyModel;
import com.junmo.core.model.ServerNodeModel;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping(value = "/get/proxy-server")
    @ResponseBody
    public ApiResult<List<ProxyVO>> getProxyServer() {
        List<ProxyVO> result = Lists.newArrayList();
        Map<String, Map<String, String>> serverMap = RegisterClient.SERVER_MAP;
        for (Map.Entry<String, Map<String, String>> entry : serverMap.entrySet()) {
            ProxyVO proxyVO = new ProxyVO();
            RegisterProxyModel registerProxyModel = RegisterClient.parseKey(entry.getKey());
            proxyVO.setProxy(registerProxyModel.getProxy());
            proxyVO.setVersion(registerProxyModel.getVersion());
            Map<String, String> nodeMap = entry.getValue();
            List<ServerNodeModel> servers = Lists.newArrayList();
            for (Map.Entry<String, String> nodeEntry : nodeMap.entrySet()) {
                String ipLinkPort = nodeEntry.getKey();
                servers.add(new ServerNodeModel(ipLinkPort));
            }
            proxyVO.setServers(servers);
            result.add(proxyVO);
        }
        return ApiResult.buildSuccess(result);
    }
}
