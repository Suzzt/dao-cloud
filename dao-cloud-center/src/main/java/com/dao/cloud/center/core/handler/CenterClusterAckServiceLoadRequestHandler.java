package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.bootstarp.DaoCloudCenterConfiguration;
import com.dao.cloud.center.core.ServiceConnectorManager;
import com.dao.cloud.core.model.AckServiceLoadRequestModel;
import com.dao.cloud.core.model.AckServiceLoadResponseModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @date 2025/5/2 21:57
 * @since 1.0.0
 */
@Slf4j
public class CenterClusterAckServiceLoadRequestHandler extends SimpleChannelInboundHandler<AckServiceLoadRequestModel> {

    private ServiceConnectorManager serviceConnectorManager;

    public CenterClusterAckServiceLoadRequestHandler(ServiceConnectorManager serviceConnectorManager) {
        this.serviceConnectorManager = serviceConnectorManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AckServiceLoadRequestModel model) {
        AckServiceLoadResponseModel ackServiceLoadResponseModel = new AckServiceLoadResponseModel();
        ackServiceLoadResponseModel.setNumber(serviceConnectorManager.count());
        DaoMessage daoMessage = new DaoMessage(DaoCloudConstant.PROTOCOL_VERSION_1, MessageType.INQUIRE_CLUSTER_LOADED_REQUEST_MESSAGE, DaoCloudCenterConfiguration.SERIALIZE_TYPE, ackServiceLoadResponseModel);
        ctx.writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("<<<<<<<<<<< Failed to send load information about the service node >>>>>>>>>>>>", future.cause());
            }
        });
    }
}