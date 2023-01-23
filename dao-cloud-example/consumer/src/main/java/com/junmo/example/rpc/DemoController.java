package com.junmo.example.rpc;

import com.junmo.boot.annotation.DaoReference;
import com.junmo.common.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: sucf
 * @date: 2023/1/12 17:38
 * @description:
 */
@RestController
public class DemoController {
    @DaoReference(proxy = "demo")
    private DemoService demoService;

    @RequestMapping("demo")
    public String demo(){
        long start = System.currentTimeMillis();
        String string1 = demoService.test("String1", 1, 1.1, 1L, true);
        long end = System.currentTimeMillis();
        return string1+"===="+(end-start);
    }
}