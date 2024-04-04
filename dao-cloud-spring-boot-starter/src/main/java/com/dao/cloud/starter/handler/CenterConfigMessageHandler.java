package com.dao.cloud.starter.handler;

import com.dao.cloud.starter.utils.DaoCloudConfig;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ConfigModel;
import com.dao.cloud.core.model.ProxyConfigModel;
import com.dao.cloud.core.util.ProxyConfigPromiseBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

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
            DaoCloudConfig.refresh(configModel.getProxyConfigModel(), configModel.getConfigValue());
        } else {
            // self pull
            DaoException daoException = configModel.getDaoException();
            if (daoException == null) {
                configPromise.setSuccess(configValue);
            } else {
                configPromise.setFailure(daoException);
            }
        }
    }
}
