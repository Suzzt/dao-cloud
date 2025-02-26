package com.dao.cloud.example.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sucf
 * @since 1.0
 */
@RestController
public class ConfigurationController {

    @Value("${user.name:JunMo}")
    private String userName;

    @RequestMapping("/config_value")
    public String getConfig(){
        return userName;
    }
}
