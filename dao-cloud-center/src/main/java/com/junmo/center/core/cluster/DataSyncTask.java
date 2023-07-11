package com.junmo.center.core.cluster;

import com.junmo.center.core.handler.SyncClusterInformationResponseHandler;
import com.junmo.core.model.ClusterSyncDataModel;
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

    private ClusterSyncDataModel clusterSyncDataModel;

    public DataSyncTask(ClusterCenterConnector clusterCenterConnector, ClusterSyncDataModel clusterSyncDataModel) {
        this.failMark = 0;
        this.clusterCenterConnector = clusterCenterConnector;
        this.clusterSyncDataModel = clusterSyncDataModel;
    }

    @Override
    public void run() {
        try {
            DefaultPromise<Set<ServerNodeModel>> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
            SyncClusterInformationResponseHandler.PROMISE_MAP.put(clusterSyncDataModel.getSequenceId(), promise);
            clusterCenterConnector.syncData(clusterSyncDataModel);
            if (!promise.await(3, TimeUnit.SECONDS) || !promise.isSuccess()) {
                log.error("<<<<<<<<<<<<<< sync data to cluster error. already error count = {} >>>>>>>>>>>>>>", failMark);
                if (failMark >= 3) {
                    log.error("data has been synchronized to the cluster more than 3 times");
                    return;
                }
                failMark++;
                SyncClusterInformationResponseHandler.PROMISE_MAP.put(clusterSyncDataModel.getSequenceId(), promise);
                clusterCenterConnector.syncData(clusterSyncDataModel);
            }
        } catch (Throwable e) {
            log.error("an unexpected situation has been interrupted", e);
            if (failMark >= 3) {
                log.error("data has been synchronized to the cluster more than 3 times");
                return;
            }
            failMark++;
            DefaultPromise<Set<ServerNodeModel>> promise = new DefaultPromise<>(clusterCenterConnector.getChannel().eventLoop());
            SyncClusterInformationResponseHandler.PROMISE_MAP.put(clusterSyncDataModel.getSequenceId(), promise);
            clusterCenterConnector.syncData(clusterSyncDataModel);
        }
    }
}
