package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.ConfigurationCenterManager;
import com.dao.cloud.core.model.ConfigurationPropertyRequestModel;
import com.dao.cloud.core.model.ConfigurationPropertyResponseModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @since 1.0
 * configuration property request handler
 */
@Slf4j
public class ConfigurationPropertyRequestHandler extends SimpleChannelInboundHandler<ConfigurationPropertyRequestModel> {

    private final ConfigurationCenterManager configurationCenterManager;

    public ConfigurationPropertyRequestHandler(ConfigurationCenterManager configurationCenterManager) {
        this.configurationCenterManager = configurationCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationPropertyRequestModel model) throws Exception {
        ConfigurationPropertyResponseModel configurationPropertyResponseModel = new ConfigurationPropertyResponseModel();
        String properties = configurationCenterManager.getConfigurationProperty(model.getProxy(), model.getGroupId(), model.getFileName());
        configurationPropertyResponseModel.setContent(properties);
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_PROPERTY_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, configurationPropertyResponseModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send configuration information data >>>>>>>>>>>>", future.cause());
            }
        });
    }
}