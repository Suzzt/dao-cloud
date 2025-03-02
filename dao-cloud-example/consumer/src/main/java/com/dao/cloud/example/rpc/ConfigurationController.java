package com.dao.cloud.example.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/12/23 22:07
 */
@RestController
public class ConfigurationController {

    @Value("${user.name:东方不败}")
    private String userName;

    @RequestMapping("/configuration_value")
    public String getConfig(){
        return userName;
    }
}
