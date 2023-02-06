package com.junmo.web;

import com.junmo.center.bootstarp.EnableDaoCloudCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: sucf
 * @date: 2023/2/6 21:03
 * @description:
 */
@SpringBootApplication
@EnableDaoCloudCenter
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
