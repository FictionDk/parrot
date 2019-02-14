package com.fictio.parrot.logic.proxy;

import org.junit.Test;

public class StaticProxyDemo {
    
    static interface IService {
        public void sayHello();
    }
    
    static class RealService implements IService {
        @Override
        public void sayHello() {
            System.out.println("real Hello");
        }
    }
    
    static class TraceProxy implements IService {
        private IService iService;
        public TraceProxy(IService iService) {
            this.iService = iService;
        }
        @Override
        public void sayHello() {
            System.out.println("entering sayHello");
            this.iService.sayHello();
            System.out.println("leaving sayHello");
        }
    }
    
    @Test
    public void demo() {
        IService realService = new RealService();
        IService proxyService = new TraceProxy(realService);
        proxyService.sayHello();
    }

}
