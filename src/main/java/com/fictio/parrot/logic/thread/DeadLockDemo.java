package com.fictio.parrot.logic.thread;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DeadLockDemo {
    private static Object lockA = new Object();
    private static Object lockB = new Object();

    private static void startThreadA() {
        Thread aThread = new Thread() {
            @Override public void run() { // 持有锁A,等待锁B
                synchronized (lockA) {
                    doSleep();
                    synchronized (lockB) {
                    }
                }
            }
        };
        aThread.start();
    }

    private static void startThreadB() throws InterruptedException {
        Thread bThread = new Thread() {
            @Override public void run() {
                synchronized (lockB) { // 持有锁B,等待锁A
                    doSleep();
                    synchronized (lockA) {
                    }
                }
            }
        };
        bThread.start();
        bThread.join(); // 使用join,可以避免@Test线程执行结束自动结束程序;
    }

    public static void doSleep(int ... arr) {
        int sl = 1;
        try {
            if(arr!=null) sl = arr[0];
            TimeUnit.SECONDS.sleep(sl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void lockTest() throws InterruptedException {
        startThreadA();
        startThreadB();
    }

    @Test public void junitTest() throws InterruptedException {
        lockTest();
    }
}
