package com.dao.cloud.starter.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.dao.cloud.core.model.LogModel;
import com.dao.cloud.core.netty.protocol.DaoMessage;
import com.dao.cloud.core.netty.protocol.MessageType;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.starter.manager.CenterChannelManager;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/8/23 12:02
 */
public class DaoCloudLogAppender extends AppenderBase<ILoggingEvent> {

    private final String node;

    public DaoCloudLogAppender(String node) {
        this.node = node;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String traceId = eventObject.getMDCPropertyMap().get("traceId");
        if (!StringUtils.hasLength(traceId)) {
            return;
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

        // 处理异常堆栈信息
        if (eventObject.getThrowableProxy() != null) {
            Throwable throwable = extractThrowable(eventObject);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            logMessage += sw;
        }

        // send trace data to center
        LogModel logModel = new LogModel();
        logModel.setTraceId(traceId);
        logModel.setNode(node);
        logModel.setHappenTime(eventObject.getTimeStamp());
        logModel.setLogMessage(logMessage);

        DaoMessage daoMessage = new DaoMessage((byte) 0, MessageType.UPLOAD_LOG_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, logModel);
        CenterChannelManager.getChannel().writeAndFlush(daoMessage);
    }

    private Throwable extractThrowable(ILoggingEvent eventObject) {
        if (eventObject.getThrowableProxy() != null) {
            return ((ch.qos.logback.classic.spi.ThrowableProxy) eventObject.getThrowableProxy()).getThrowable();
        }
        return null;
    }
}
