package com.dao.cloud.starter.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.io.FileUtil;
import com.dao.cloud.core.model.LogModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
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
        String traceId = eventObject.getMDCPropertyMap().get("traceId");
        // todo 后面再维护
        String stage = "2-2";
        if (!StringUtils.hasLength(traceId)) {
            return;
        }
        String filePath = logStoragePath + File.separator + traceId;
        String separator = "-";
        for (String str : stage.split(separator)) {
            filePath += File.separator + str;
        }
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")
                .format(new java.util.Date(eventObject.getTimeStamp()));
        String logLevel = eventObject.getLevel().toString();
        String message = eventObject.getFormattedMessage();
        int lineNumber = eventObject.getCallerData()[0].getLineNumber();
        String fileName = eventObject.getCallerData()[0].getFileName();

        // 构建所需格式的日志输出
        String logMessage = String.format("%s %s (%s:%d)- %s%n",
                timestamp,
                logLevel,
                fileName,
                lineNumber,
                message
        );

        // 将格式化后的日志写入指定文件
        FileUtil.writeUtf8String(logMessage, filePath);

        // 将格式化的日志写入文件
        FileUtil.writeUtf8String(logMessage.toString(), filePath);
        // send trace data to center
        LogModel logModel = new LogModel();
        logModel.setTraceId(traceId);
        logModel.setStage(stage);
        logModel.setNode(node);
        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.UPLOAD_LOG_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, logModel);
        CenterChannelManager.getChannel().writeAndFlush(daoMessage);
    }
}
