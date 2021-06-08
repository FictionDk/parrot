package com.fictio.parrot.logic.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.fictio.parrot.logic.thread.DeadLockDemo;

class Vistor implements Runnable {
    private AtomicInteger counter;
    public Vistor(AtomicInteger counter) {
        this.counter = counter;
    }
    @Override
    public void run() {
        for(int i = 0; i < 100; i++) counter.incrementAndGet();
        System.out.println(Thread.currentThread().getName()+"_"+counter.get());
    }
}

public class AtomicIntegerDemo {
    private static AtomicInteger counter = new AtomicInteger(0);
    @Test public void tests() throws InterruptedException {
        Vistor v = new Vistor(counter);
        for(int i = 0; i < 100; i++) {
            Thread t = new Thread(v,"T_"+i);
            t.start();
            t.join();  // 如果没有join,会有部分线程在主线程结束后打印
        }
        System.out.println(counter.get());
        DeadLockDemo.doSleep(2);
    }
}
