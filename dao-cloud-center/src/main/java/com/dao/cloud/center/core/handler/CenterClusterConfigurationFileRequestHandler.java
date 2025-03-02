package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.ConfigurationCenterManager;
import com.dao.cloud.core.model.ConfigurationFileInformationModel;
import com.dao.cloud.core.model.ConfigurationFilePullMarkModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author sucf
 * @since 1.0
 */
@Slf4j
public class CenterClusterConfigurationFileRequestHandler extends SimpleChannelInboundHandler<ConfigurationFilePullMarkModel> {

    private final ConfigurationCenterManager configurationCenterManager;

    public CenterClusterConfigurationFileRequestHandler(ConfigurationCenterManager configurationCenterManager) {
        this.configurationCenterManager = configurationCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigurationFilePullMarkModel configurationFilePullMarkModel) throws Exception {
        Set<ConfigurationFileInformationModel> files = configurationCenterManager.fullFileInformation();
        DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.INQUIRE_CLUSTER_FULL_CONFIGURATION_FILE_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, files);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send configuration file information data >>>>>>>>>>>>", future.cause());
            }
        });
    }
}
