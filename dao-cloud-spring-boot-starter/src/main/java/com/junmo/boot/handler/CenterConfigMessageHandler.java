package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.DaoConfig;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sucf
 * @date: 2023/2/12 16:58
 * @description:
 */
@Slf4j
public class CenterConfigMessageHandler extends SimpleChannelInboundHandler<ConfigModel> {

    public static final Map<ProxyConfigModel, Promise<String>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String configValue = configModel.getConfigValue();
        Promise<String> configPromise = PROMISE_MAP.remove(proxyConfigModel);
        if (configPromise == null) {
            // subscribe push
            DaoConfig.refresh(configModel.getProxyConfigModel(), configModel.getConfigValue());
        } else {
            // self pull
            String errorMessage = configModel.getErrorMessage();
            if (StringUtils.hasLength(errorMessage)) {
                configPromise.setFailure(new DaoException(errorMessage));
            } else {
                configPromise.setSuccess(configValue);
            }
        }
    }
}
