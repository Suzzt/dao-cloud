package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.ConfigurationCenterManager;
import com.dao.cloud.core.model.ConfigurationFileInformationRequestModel;
import com.dao.cloud.core.model.ConfigurationFileInformationResponseModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
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
        ConfigurationFileInformationResponseModel configurationFileInformationResponseModel = new ConfigurationFileInformationResponseModel();
        configurationFileInformationResponseModel.setFileNames(configurationCenterManager.getConfigurationFile(model.getProxy(), model.getGroupId()));
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.PULL_CENTER_CONFIGURATION_FILE_INFORMATION_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, configurationFileInformationResponseModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send configuration information data >>>>>>>>>>>>", future.cause());
            }
        });
    }
}
