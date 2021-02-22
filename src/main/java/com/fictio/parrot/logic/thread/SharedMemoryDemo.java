package com.fictio.parrot.logic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> 竞态条件
 */
class CounterThread implements Runnable {
    private static int counter = 0;
    @Override
    public void run() {
        for(int i = 0; i < 10000; i++) counter ++;

    }
    public static int getCounter() {
        return counter;
    }
}

class VisibilityThread extends Thread {
    // 可通过synchronized或volatie保证内存可见性,从性能成本考虑,应该用volatie
    private boolean shutdown = false;
    @Override public void run() {
        while(!shutdown) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(this.getName()+":"+this.isAlive());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("exit demo");
    }
    public void shutDown() {
        shutdown = true;
    }
}


/**
 * <p> 内存共享
 */
@Slf4j
public class SharedMemoryDemo {
    private static int shared = 0;
    // 实例方法上 用this对象作为锁
    // 静态方法上 用类对象作为锁
    private synchronized static void incrShared() {
        shared ++;
    }
    static class ChildThread extends Thread {
        List<String> list;
        public ChildThread(String name,List<String> list) {
            super(name);
            this.list = list;
        }
        @Override
        public void run(){
            incrShared();
            // 需要注意锁,用this作为对象锁时,会造成锁失效的
            synchronized (SharedMemoryDemo.class) {  // 保护同一个对象的方法调用,确保同时只能有一个线程执行
                list.add(Thread.currentThread().getName());
            }
        }
    }

    // 内存共享操作
    private int optTest() throws InterruptedException {
        List<String> list = new ArrayList<>();
        Thread t1 = new ChildThread("A", list);
        Thread t2 = new ChildThread("B", list);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("shared = {}; list= {}", shared, list);
        int result = shared;
        return result;
    }

    @Test public void batch() throws InterruptedException {
        int count = 0;
        int total = 200;
        for(int i = 0; i < total; i++) {
            int r = optTest();
            if(r == 2 * (i+1)) count++;
        }
        log.debug("sucess rate = {} %", (count*1.0/total) * 100);
    }

    @Test public void test() throws InterruptedException {
        optTest();
    }

    @Test public void counterBatch() {
        int num = 1000;
        CounterThread[] threads = new CounterThread[num];
        for(int i = 0; i < num; i++) {
            threads[i] = new CounterThread();
            threads[i].run();
            log.debug("current counter = {}", CounterThread.getCounter());
        }
        log.debug("counter = {}", CounterThread.getCounter());
    }

    @Test
    public void visibiltyTest() throws InterruptedException {
        VisibilityThread t = new VisibilityThread();
        t.start();
        t.shutDown();
        t.join();  // 如果使用main函数,不需要join;如果用@Test,需要join确保当前线程等待t线程
        TimeUnit.SECONDS.sleep(1);
    }
}
