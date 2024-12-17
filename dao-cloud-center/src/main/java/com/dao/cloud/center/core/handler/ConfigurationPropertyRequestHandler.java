package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.ConfigurationCenterManager;
import com.dao.cloud.core.model.ConfigurationPropertyRequestModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: sucf
 * @date: 2024/12/17 23:34
 * @description: configuration property request handler
 */
public class ConfigurationPropertyRequestHandler extends SimpleChannelInboundHandler<ConfigurationPropertyRequestModel> {

    private final ConfigurationCenterManager configurationCenterManager;

    public ConfigurationPropertyRequestHandler(ConfigurationCenterManager configurationCenterManager) {
        this.configurationCenterManager = configurationCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationPropertyRequestModel model) throws Exception {
    }
}