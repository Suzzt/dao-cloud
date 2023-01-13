package com.junmo.example.rpc;

import com.junmo.boot.annotation.DaoService;
import com.junmo.common.DemoService;

/**
 * @author: sucf
 * @date: 2023/1/12 17:33
 * @description:
 */
@DaoService
public class DemoServiceImpl implements DemoService {
    @Override
    public String test(String string1, int int1, double double1, long long1, boolean flag) {
        System.out.println(string1 + int1 + double1 + long1 + flag);
        return string1 + int1 + double1 + long1 + flag;
    }
}
