package com.junmo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: sucf
 * @date: 2022/11/19 18:10
 * @description:
 */
@SpringBootApplication
@Slf4j
public class ConfigApplication {
    public static void main(String[] args) {
        log.info("111111");
        SpringApplication.run(ConfigApplication.class, args);
    }
}
