package com.junmo.center.core.cluster;

import com.google.gson.Gson;
import com.junmo.center.core.handler.SyncClusterInformationResponseHandler;
import com.junmo.core.model.ClusterSyncDataRequestModel;
import com.junmo.core.model.ServerNodeModel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/7/11 20:44
 * @description: cluster data sync task
 */
@Slf4j
public class DataSyncTask implements Runnable {

    private int failMark;

    private ClusterCenterConnector clusterCenterConnector;

    private ClusterSyncDataRequestModel clusterSyncDataRequestModel;

    public DataSyncTask(ClusterCenterConnector clusterCenterConnector, ClusterSyncDataRequestModel clusterSyncDataRequestModel) {
        this.failMark = 1;
        this.clusterCenterConnector = clusterCenterConnector;
        this.clusterSyncDataRequestModel = clusterSyncDataRequestModel;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        while (true) {
            try {
                DefaultPromise<Set<ServerNodeModel>> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
                SyncClusterInformationResponseHandler.PROMISE_MAP.put(clusterSyncDataRequestModel.getSequenceId(), promise);
                clusterCenterConnector.syncData(clusterSyncDataRequestModel);
                if (!promise.await(3, TimeUnit.SECONDS) || !promise.isSuccess()) {
                    log.error("<<<<<<<<<<<<<< sync data = {} to cluster error. already error count = {} >>>>>>>>>>>>>>", gson.toJson(clusterSyncDataRequestModel), failMark);
                    if (failMark >= 3) {
                        log.error("data = {} has been synchronized to the cluster more than 3 times", gson.toJson(clusterSyncDataRequestModel));
                        return;
                    }
                    failMark++;
                }
                log.info("sync data = {} to cluster success", gson.toJson(clusterSyncDataRequestModel));
                break;
            } catch (Throwable e) {
                log.error("an unexpected situation has been interrupted", e);
                if (failMark >= 3) {
                    log.error("data = {} has been synchronized to the cluster more than 3 times", gson.toJson(clusterSyncDataRequestModel));
                    return;
                }
                failMark++;
            }
        }
    }
}
