package com.junmo.center.web.controller;

import com.junmo.center.core.*;
import com.junmo.center.web.interceptor.PermissionInterceptor;
import com.junmo.center.web.interceptor.Permissions;
import com.junmo.core.ApiResult;
import com.junmo.core.enums.CodeEnum;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: sucf
 * @date: 2023/07/28 11:12
 * @description: 页面请求处理
 */
@Controller
@RequestMapping(value = "dao-cloud")
public class IndexController {

    private ConfigCenterManager configCenterManager;

    public GatewayCenterManager gatewayCenterManager;

    public IndexController(ConfigCenterManager configCenterManager, GatewayCenterManager gatewayCenterManager) {
        this.configCenterManager = configCenterManager;
        this.gatewayCenterManager = gatewayCenterManager;
    }

    @RequestMapping(value = {"", "/", "index"})
    public String index(Model model) {
        // 当前节点存活集群数
        model.addAttribute("aliveClusterNodeNum", CenterClusterManager.aliveNodeSize());
        // 当前节点存活集群数
        model.addAttribute("gatewayNodeNum", RegisterCenterManager.gatewayCountNodes());
        // 注册服务数
        model.addAttribute("providerNum", RegisterCenterManager.nodes());
        // 注册方法可调用数
        model.addAttribute("methodNum", RegisterCenterManager.methods());
        // 配置条数
        model.addAttribute("configNum", configCenterManager.size());
        // 每个配置服务订阅数的总和(不去重)
        model.addAttribute("configSubscribeNum", ConfigChannelManager.size());
        return "index";
    }

    @RequestMapping("/toLogin")
    @Permissions(limit = false)
    public String toLogin(HttpServletRequest request) {
        if (PermissionInterceptor.ifLogin(request)) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @Permissions(limit = false)
    public ApiResult<String> login(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        // valid
        if (PermissionInterceptor.ifLogin(request)) {
            return ApiResult.buildSuccess();
        }

        // param
        if (userName == null || userName.trim().length() == 0 || password == null || password.trim().length() == 0) {
            return ApiResult.buildFail(CodeEnum.PARAMETER_ERROR.getCode(), "请输入账号密码");
        }
        boolean ifRem = (ifRemember != null && "on".equals(ifRemember)) ? true : false;

        // do login
        boolean loginRet = PermissionInterceptor.login(response, userName, password, ifRem);
        if (!loginRet) {
            return ApiResult.buildFail(CodeEnum.PARAMETER_ACCOUNT_ERROR);
        }
        return ApiResult.buildSuccess();
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @Permissions(limit = false)
    public ApiResult<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (PermissionInterceptor.ifLogin(request)) {
            PermissionInterceptor.logout(request, response);
        }
        return ApiResult.buildSuccess();
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping("registry")
    public String registry(Model model) {
        return "registry/registry.index";
    }

    @RequestMapping("config")
    public String config() {
        return "config/config.index";
    }

    @RequestMapping("gateway")
    public String gateway() {
        return "gateway/gateway.index";
    }
}
