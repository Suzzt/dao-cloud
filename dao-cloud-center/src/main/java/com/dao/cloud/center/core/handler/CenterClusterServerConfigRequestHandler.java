package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.RegisterCenterManager;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.ServerConfigModel;
import com.dao.cloud.core.model.ServerConfigPullMarkModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/4/1 14:16
 * @description: Service configuration pull request
 */
@Slf4j
public class CenterClusterServerConfigRequestHandler extends SimpleChannelInboundHandler<ServerConfigPullMarkModel> {

    private RegisterCenterManager registerCenterManager;

    public CenterClusterServerConfigRequestHandler(RegisterCenterManager registerCenterManager) {
        this.registerCenterManager = registerCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerConfigPullMarkModel serverConfigPullMarkModel) throws Exception {
        DaoMessage daoMessage;
        ServerConfigModel serverConfigModel = new ServerConfigModel();
        try {
            serverConfigModel.setServerConfig(registerCenterManager.getConfig());
        } catch (Exception e) {
            serverConfigModel.setDaoException(new DaoException(CodeEnum.PULL_SERVER_CONFIG_ERROR));
        }
        daoMessage = new DaoMessage((byte) 1, MessageType.INQUIRE_CLUSTER_FULL_SERVER_CONFIG_RESPONSE_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, serverConfigModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send service configuration data >>>>>>>>>>>>", future.cause());
            }
        });
    }
}
