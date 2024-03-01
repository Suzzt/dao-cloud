package com.dao.cloud.center.core.handler;

import com.dao.cloud.center.core.ConfigCenterManager;
import com.google.common.collect.Lists;
import com.dao.cloud.core.model.ConfigMarkModel;
import com.dao.cloud.core.model.ConfigModel;
import com.dao.cloud.core.model.FullConfigModel;
import com.dao.cloud.core.model.ProxyConfigModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/7/4 15:40
 * @description: 处理拉取配置请求
 */
@Slf4j
public class PullConfigRequestHandler extends SimpleChannelInboundHandler<ConfigMarkModel> {

    private ConfigCenterManager configCenterManager;

    public PullConfigRequestHandler(ConfigCenterManager configCenterManager) {
        this.configCenterManager = configCenterManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfigMarkModel configMarkModel) {
        FullConfigModel fullConfigModel = new FullConfigModel();
        List<ConfigModel> configModels = Lists.newArrayList();
        Map<ProxyConfigModel, String> configMap = configCenterManager.getFullConfig();
        for (Map.Entry<ProxyConfigModel, String> entry : configMap.entrySet()) {
            ConfigModel configModel = new ConfigModel();
            configModel.setProxyConfigModel(entry.getKey());
            configModel.setConfigValue(entry.getValue());
            configModels.add(configModel);
        }
        fullConfigModel.setConfigModels(configModels);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.INQUIRE_CLUSTER_FULL_CONFIG_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, fullConfigModel);
        ctx.channel().writeAndFlush(daoMessage).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("send full config data error", future.cause());
            }
        });
    }
}
