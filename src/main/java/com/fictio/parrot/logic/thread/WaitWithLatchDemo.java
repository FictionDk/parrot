package com.fictio.parrot.logic.thread;

import org.junit.Test;

class FireFlag {  // 出发flag
    private volatile boolean fired = false;
    public synchronized void waitForFire() throws InterruptedException {
        while(!fired) wait();
    }
    public synchronized void fire() {
        this.fired = true;
        notifyAll();
    }
}

class LatchFlag {  // 结束flag
    private int count;
    public LatchFlag(int count) {
        this.count = count;
    }
    public synchronized void await() throws InterruptedException {
        while(count > 0) wait();
    }
    public synchronized void countDown() {
        count --;
        if(count <= 0) notifyAll();
    }
}

class Worker implements Runnable {
    private LatchFlag flag;
    public Worker(LatchFlag latchFlag) {
        this.flag = latchFlag;
    }
    @Override
    public void run() {
        try {
            Thread.sleep((int)Math.random()*100);
            flag.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class Racer implements Runnable {
    private FireFlag flag;
    public Racer(FireFlag fireFlag) {
        this.flag = fireFlag;
    }
    @Override
    public void run() {
        try {
            flag.waitForFire();
            System.out.println(Thread.currentThread().getName() + " Started At "+System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class WaitWithLatchDemo {
    // 所有用户一起出发
    @Test public void waitFireTest() {
        FireFlag flag = new FireFlag();
        Racer r = new Racer(flag);
        for(int i = 0 ; i < 10; i++) new Thread(r,"T_"+i).start();
        DeadLockDemo.doSleep(1);
        flag.fire();
        DeadLockDemo.doSleep(1);
    }


    @Test public void waitLatchTest() throws InterruptedException {
        int countNum = 100;
        LatchFlag flag = new LatchFlag(countNum);
        Worker worker = new Worker(flag);
        for(int i = 0; i < countNum; i++) new Thread(worker, "T_"+i).start();
        flag.await();
        System.out.println("Ending");
    }
}
