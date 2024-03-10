package com.dao.cloud.gateway;

import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.HttpGenericInvokeUtils;
import com.dao.cloud.gateway.intercept.Interceptor;
import com.dao.cloud.gateway.manager.GatewayConfig;
import com.dao.cloud.gateway.manager.GatewayConfigManager;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.bootstrap.manager.ClientManager;
import com.dao.cloud.starter.bootstrap.unit.ClientInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;

/**
 * @author: sucf
 * @date: 2023/12/28 14:55
 * @description: 转发分发处理器
 */
@RestController
@Slf4j
public class Dispatcher {

    private DaoLoadBalance daoLoadBalance;

    private List<Interceptor> interceptors;

    public Dispatcher(DaoLoadBalance daoLoadBalance, List<Interceptor> interceptors) {
        this.daoLoadBalance = daoLoadBalance;
        this.interceptors = interceptors;
    }

    /**
     * 网关请求主入口(get)
     *
     * @param proxy
     * @param provider
     * @param version
     * @param request
     * @param response
     */
    @RequestMapping(value = "api/{proxy}/{provider}/{version}/{method}", method = RequestMethod.GET)
    public void goGet(@PathVariable String proxy, @PathVariable String provider, @PathVariable() String version,
                      @PathVariable String method, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(method)) {
            throw new DaoException(CodeEnum.GATEWAY_REQUEST_PARAM_DELETION.getCode(), CodeEnum.GATEWAY_REQUEST_PARAM_DELETION.getText());
        }
        byte v = Byte.valueOf(version);
        filter(new ProxyProviderModel(proxy, provider, v));
        DaoCloudServletRequest requestModel = HttpGenericInvokeUtils.buildRequest(request);
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel(provider, v, method, requestModel);
        this.doService(proxy, gatewayRequestModel, response);
    }

    /**
     * 网关请求主入口(post)
     *
     * @param proxy
     * @param provider
     * @param version
     * @param request
     * @param response
     */
    @RequestMapping(value = "api/{proxy}/{provider}/{version}/{method}", method = RequestMethod.POST)
    public void goPost(@PathVariable String proxy, @PathVariable String provider, @PathVariable() String version,
                       @PathVariable String method, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!StringUtils.hasLength(proxy) || !StringUtils.hasLength(provider) || !StringUtils.hasLength(version) || !StringUtils.hasLength(method)) {
            throw new DaoException(CodeEnum.GATEWAY_REQUEST_PARAM_DELETION.getCode(), CodeEnum.GATEWAY_REQUEST_PARAM_DELETION.getText());
        }
        byte v = Byte.valueOf(version);
        filter(new ProxyProviderModel(proxy, provider, v));
        DaoCloudServletRequest requestModel = HttpGenericInvokeUtils.buildRequest(request);
        GatewayRequestModel gatewayRequestModel = new GatewayRequestModel(provider, v, method, requestModel);
        this.doService(proxy, gatewayRequestModel, response);
    }

    public void filter(ProxyProviderModel proxyProviderModel) {
        // 限流过滤
        GatewayConfig gatewayConfig = GatewayConfigManager.getGatewayConfig(proxyProviderModel);
        if (gatewayConfig != null && gatewayConfig.getLimiter() != null) {
            if (!gatewayConfig.getLimiter().tryAcquire()) {
                throw new DaoException(CodeEnum.GATEWAY_REQUEST_LIMIT.getCode(), CodeEnum.GATEWAY_REQUEST_LIMIT.getText());
            }
        }

        // 网关拦截过滤
        for (Interceptor interceptor : interceptors) {
            if (!interceptor.intercept().getSuccess()) {
                throw new DaoException(CodeEnum.GATEWAY_INTERCEPTION_FAIL.getCode(), CodeEnum.GATEWAY_INTERCEPTION_FAIL.getText());
            }
        }
    }


    public void doService(String proxy, GatewayRequestModel gatewayRequestModel, HttpServletResponse response) throws InterruptedException {
        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, gatewayRequestModel.getProvider(), gatewayRequestModel.getVersion());
        Set<ServerNodeModel> providerNodes = ClientManager.getProviderNodes(proxyProviderModel);
        if (providerNodes == null) {
            throw new DaoException(CodeEnum.GATEWAY_SERVICE_NOT_EXIST.getCode(), CodeEnum.GATEWAY_SERVICE_NOT_EXIST.getText());
        }

        GatewayConfig gatewayConfig = GatewayConfigManager.getGatewayConfig(proxyProviderModel);
        // gateway timout config
        Long timeout;
        if (gatewayConfig == null) {
            // default timeout 30s
            timeout = 30L;
        } else {
            timeout = gatewayConfig.getTimeout();
            // default timeout 30s
            if (timeout == null || timeout <= 0) {
                timeout = 30L;
            }
        }

        // 发起转发路由请求
        ClientInvoker clientInvoker = new ClientInvoker(proxyProviderModel, daoLoadBalance, DaoCloudConstant.DEFAULT_SERIALIZE, timeout);
        DaoCloudServletResponse result;
        result = (DaoCloudServletResponse) clientInvoker.invoke(gatewayRequestModel);
        Optional.ofNullable(result.getHeads()).orElse(Collections.emptyMap())
                .forEach(response::addHeader);
        if (Objects.nonNull(result.getBodyData())) {
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(result.getBodyData());
            } catch (Exception e) {
                throw new DaoException(CodeEnum.GATEWAY_REQUEST_ERROR);
            }
        }
    }
}
