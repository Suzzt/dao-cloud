package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.DaoConfig;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
import com.junmo.core.util.ProxyConfigPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author: sucf
 * @date: 2023/2/12 16:58
 * @description:
 */
@Slf4j
public class CenterConfigMessageHandler extends SimpleChannelInboundHandler<ConfigModel> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String configValue = configModel.getConfigValue();
        Promise<String> configPromise = ProxyConfigPromiseBuffer.getInstance().remove(proxyConfigModel);
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
