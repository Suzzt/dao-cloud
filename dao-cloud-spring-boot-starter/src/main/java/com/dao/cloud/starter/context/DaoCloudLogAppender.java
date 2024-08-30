package com.dao.cloud.starter.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.io.FileUtil;
import com.dao.cloud.core.model.LogModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author: sucf
 * @date: 2024/8/23 12:02
 * @description:
 */
public class DaoCloudLogAppender extends AppenderBase<ILoggingEvent> {

    private final String logStoragePath;

    private final String node;

    public DaoCloudLogAppender(String logStoragePath, String node) {
        this.logStoragePath = logStoragePath;
        this.node = node;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String traceId = MDC.get("traceId");
        String stage = MDC.get("stage");
        if (!StringUtils.hasLength(traceId)) {
            return;
        }
        String filePath = logStoragePath + File.separator + traceId;
        String separator = "-";
        for (String str : stage.split(separator)) {
            filePath += File.separator + str;
        }
        FileUtil.writeUtf8String("[" + traceId + "]=" + eventObject.getFormattedMessage(), filePath);
        // send trace data to center
        LogModel logModel = new LogModel();
        logModel.setTraceId(traceId);
        logModel.setStage(stage);
        logModel.setNode(node);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.UPLOAD_LOG_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, logModel);
        CenterChannelManager.getChannel().writeAndFlush(daoMessage);
    }
}
