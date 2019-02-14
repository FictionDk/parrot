package com.fictio.parrot.logic.proxy;

import java.lang.reflect.Method;

import org.junit.Test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLibProxyDemo {
    
    static class RealService  {
        public void sayHello() {
            System.out.println("real Hello");
        }
    }
    
    static class SimpleInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("entering "+method.getName());
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("leaving "+method.getName());
            return result;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T getProxy(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new SimpleInterceptor());
        return (T) enhancer.create();
    }
    
    
    @Test
    public void demo() {
        RealService proxyService = getProxy(RealService.class);
        proxyService.sayHello();
    }

}
