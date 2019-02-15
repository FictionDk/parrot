package com.fictio.parrot.demo;

import java.time.LocalDateTime;
import org.junit.Test;

public class CommonTestDemo {
    
    @Test
    public void test() {
        LocalDateTime dt2 = LocalDateTime.now().plusDays(-3);
        LocalDateTime dt3 = LocalDateTime.now();
        System.out.println(dt2.toString()+" is after "+dt3.toString()+dt2.isAfter(dt3));
    }
    
    @Test
    public void finallyTest() {
        int x = addTest();
        System.out.println(x);
    }
    
    static class Example extends Thread {
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("run");
        }
    }
    
    @Test
    public void theadTest() {
        Example ep = new Example();
        ep.run();
        System.out.println("main");
    }
    
    @Test
    public void syncTest() {
        final Object obj = new Object();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                synchronized (obj) {
                    try {
                        obj.wait();
                        System.out.println("thread 1 wake up");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread() {
            @Override
            public void run() {
                synchronized (obj) {
                    obj.notifyAll();
                    System.out.println("t2 send notify ");
                };
            }
        };
        t2.start();
    }
    
    public int addTest() {
        int x = 1;
        try {
            x++;
            //先将待return的结果缓存,再执行finally
            return x;
        } finally {
            x++;
            //如果有return,覆盖try中的return
            //return x;
        }
    }
    
}
