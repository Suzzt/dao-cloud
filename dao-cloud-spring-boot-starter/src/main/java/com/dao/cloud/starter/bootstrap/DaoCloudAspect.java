package com.dao.cloud.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: sucf
 * @date: 2024/8/23 00:01
 * @description: dao cloud aspect
 */
@Aspect
@Component
@Slf4j
public class DaoCloudAspect {

    @Pointcut("execution(* org.slf4j.Logger.info(..)) || execution(* org.slf4j.Logger.error(..))|| execution(* org.slf4j.Logger.debug(..))|| execution(* org.slf4j.Logger.warn(..))|| execution(* org.slf4j.Logger.trace(..))")
    public void log() {
    }

    @Before("log()")
    public void before() {
        System.err.println("Executing custom logic before logging...");
    }
}
