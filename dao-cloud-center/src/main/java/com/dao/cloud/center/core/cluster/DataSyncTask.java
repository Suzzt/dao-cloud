package com.dao.cloud.center.core.cluster;

import com.dao.cloud.core.util.GsonUtils;
import com.google.gson.Gson;
import com.dao.cloud.core.model.AbstractShareClusterRequestModel;
import com.dao.cloud.core.util.LongPromiseBuffer;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/7/11 20:44
 * cluster data sync task
 */
@Slf4j
public class DataSyncTask implements Runnable {

    private int failMark;

    private final ClusterCenterConnector clusterCenterConnector;

    private final AbstractShareClusterRequestModel requestModel;

    public DataSyncTask(ClusterCenterConnector clusterCenterConnector, AbstractShareClusterRequestModel requestModel) {
        this.failMark = 1;
        this.clusterCenterConnector = clusterCenterConnector;
        this.requestModel = requestModel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Promise promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
                LongPromiseBuffer.getInstance().put(requestModel.getSequenceId(), promise);
                clusterCenterConnector.share(requestModel);
                if (!promise.await(3, TimeUnit.SECONDS) || !promise.isSuccess()) {
                    log.error("<<<<<<<<<<<<<< sync data = {} to cluster error. already error count = {} >>>>>>>>>>>>>>", GsonUtils.toJson(requestModel), failMark, promise.cause());
                    if (failMark >= 3) {
                        log.error("data = {} has been synchronized to the cluster more than 3 times", GsonUtils.toJson(requestModel));
                        return;
                    }
                    failMark++;
                }
                log.info("sync data = {} to cluster success", GsonUtils.toJson(requestModel));
                break;
            } catch (Throwable e) {
                log.error("an unexpected situation has been interrupted", e);
                if (failMark >= 3) {
                    log.error("data = {} has been synchronized to the cluster more than 3 times", GsonUtils.toJson(requestModel));
                    return;
                }
                failMark++;
            }
        }
    }
}
