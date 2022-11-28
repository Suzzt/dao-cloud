package com.junmo.boot;

import com.junmo.boot.proxy.ProxyFactory;
import test.TestService;

/**
 * @author: sucf
 * @date: 2022/10/29 09:57
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        TestService testService = ProxyFactory.build(TestService.class);
        System.out.println(testService.test(1, "DAO"));
    }
}
