package com.fictio.parrot.logic.proxy.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Aspect({SerivceA.class,SerivceB.class})
@Slf4j
public class ServiceLogAspect {
    
    public static void before(Object obj,Method method,Object[] args) {
        log.info("entering {}::{},args:{} ",method.getDeclaringClass(),method.getName(),Arrays.toString(args));
    }
    
    public static void after(Object obj,Method method,Object result) {
        log.info("leaving {}::{},result:{} ",method.getDeclaringClass(),method.getName(),result);
    }
    
}
