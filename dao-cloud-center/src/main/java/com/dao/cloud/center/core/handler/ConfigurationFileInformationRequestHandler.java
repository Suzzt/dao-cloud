package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.ConfigurationCenterManager;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/12/7 16:27
 * @description: configuration file information request handler
 */
@Slf4j
public class ConfigurationFileInformationRequestHandler extends SimpleChannelInboundHandler<ConfigurationFileInformationRequestModel> {

    private final ConfigurationCenterManager configurationCenterManager;

    public ConfigurationFileInformationRequestHandler(ConfigurationCenterManager configurationCenterManager) {
        this.configurationCenterManager = configurationCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationFileInformationRequestModel model) throws Exception {
        configurationCenterManager.getConfigurationFile(model.getProxy(), model.getGroupId());
    }
}
