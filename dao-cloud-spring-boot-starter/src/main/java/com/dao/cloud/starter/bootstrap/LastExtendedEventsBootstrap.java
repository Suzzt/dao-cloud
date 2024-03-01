package com.dao.cloud.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author: sucf
 * @date: 2024/1/14 17:39
 * @description: The last step is to expand the customized Bootstrap
 * If you want to do something after all components of dao-cloud are loaded, please implement the internal method of interface 'ExtendedEvents'
 */
@Slf4j
public class LastExtendedEventsBootstrap implements ApplicationListener<ContextRefreshedEvent> {
    public interface CustomExtension {
        /**
         * Execute your own business development content
         */
        void event();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
