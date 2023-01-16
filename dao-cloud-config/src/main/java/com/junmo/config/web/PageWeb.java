package com.junmo.config.web;

import com.junmo.common.ApiResult;
import com.junmo.config.register.Register;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description:
 */
@RestController
public class PageWeb {

    @RequestMapping(value = "/get/proxy-server")
    @ResponseBody
    public ApiResult getProxyServer() {
        return ApiResult.buildSuccess(Register.SERVER_MAP);
    }

    @RequestMapping(value = "/get/server-nodes")
    @ResponseBody
    public ApiResult getServerNodes(@RequestParam("proxy") String proxy) {
        return ApiResult.buildSuccess(Register.getServers(proxy));
    }
}
