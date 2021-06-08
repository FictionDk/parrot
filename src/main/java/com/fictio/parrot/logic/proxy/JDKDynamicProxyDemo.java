package com.fictio.parrot.logic.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class JDKDynamicProxyDemo {

    static interface IService {
        public void sayHello();
    }

    static class RealService implements IService {
        @Override
        public void sayHello() {
            System.out.println("real Hello");
        }
    }

    static class SimpleInvocationHandler implements InvocationHandler {
        private Object realObj;
        public SimpleInvocationHandler(Object realObj) {
            this.realObj = realObj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("entering "+method.getName());
            Object result = method.invoke(realObj, args);
            System.out.println("leaving "+method.getName());
            return result;
        }
    }

    /**
     * newProxyInstance(ClassLoader loader,class<?>[] interfaces, InvocationHandler h)
     * loader 类加载器
     * interfaces 代理要实现的接口列表
     * handler 代理方法的实际调用者,继承InvocationHandler接口并实现
     *
     * return Object: 可以强制转换未interfaces数组中的某个接口类型,注意的是不能强转为实现类的类型
     */
    @Test
    public void demo() {
        IService realService = new RealService();
        IService proxyService = (IService) Proxy.newProxyInstance(
                IService.class.getClassLoader(), new Class<?>[] {IService.class},
                new SimpleInvocationHandler(realService));
        proxyService.sayHello();

        proxyService = (IService) Proxy.newProxyInstance(JDKDynamicProxyDemo.class.getClassLoader(),
                new Class<?>[] {IService.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("in_entering "+method.getName());
                        Object result = method.invoke(realService, args);
                        System.out.println("in_leaving "+method.getName());
                        return result;
                    }
                });
        proxyService.sayHello();
    }

}
