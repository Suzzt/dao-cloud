package com.dao.cloud.example;

import com.dao.cloud.center.bootstarp.EnableDaoCloudCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/6 21:03
 */
@SpringBootApplication
// 开启dao-cloud center注解
@EnableDaoCloudCenter
public class DaoCloudAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(DaoCloudAdminApplication.class, args);
    }
}
