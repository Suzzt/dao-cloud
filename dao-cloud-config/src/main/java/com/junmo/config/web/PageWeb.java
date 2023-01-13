package com.junmo.config.web;

import com.junmo.common.ApiResult;
import com.junmo.config.register.RegisterManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: sucf
 * @date: 2022/11/19 18:16
 * @description:
 */
@RestController
public class PageWeb {

    @RequestMapping(value = "/get/server-info")
    @ResponseBody
    public ApiResult getServerInfo(){
       return ApiResult.buildSuccess(RegisterManager.serverMap);
    }
}
