package com.dao.cloud.starter;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.starter.manager.CenterChannelManager;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Perform the initial dao-cloud initialization work.
 *
 * @author sucf
 * @since 1.0
 */
public class DaoCloudInitializationListener implements SpringApplicationRunListener, Ordered {

    public DaoCloudInitializationListener(SpringApplication application, String[] args) {
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        initDaoCloud(environment);
    }

    private void initDaoCloud(ConfigurableEnvironment environment) {
        String ip = environment.getProperty("dao-cloud.center.ip");
        try {
            // init the connection dao-cloud center cluster
            CenterChannelManager.init(ip);
        } catch (InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}