package com.fictio.parrot.logic.thread;

import org.junit.Test;

class WaitThread extends Thread {
    private volatile boolean fired = false;

    @Override public void run() {
        try {
            synchronized (this) {
                while(!fired) wait(); // 1.wt线程状态由RUNNING->WAITING,释放锁 ||3. 再次进入代码块,!fired不符合,代码进入后续
            }
            System.out.println(this.getState()); // C.RUNNABLE
            System.out.println("fired..");  // 4. 代码执行,打印fired
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalMonitorStateException e) {
            // fired变量被synchronized保护,如果不在synchronized内调用wait/notify时会报错
            System.out.println(">>>> "+e.toString());
        }
    }

    public synchronized void fire() {  // 2.获取锁,修改wt对象内的变量fired由false->true
        this.fired = true;
        System.out.println("fire!");
        notify();
    }
}

public class WaitAndNotifyDemo {

    @Test public void waitTest() {
        WaitThread wt = new WaitThread();
        wt.start();
        DeadLockDemo.doSleep(1); System.out.println(wt.getState()); // A.WAITING
        wt.fire(); System.out.println(wt.getState()); // B.BLOCKED
        DeadLockDemo.doSleep(2); System.out.println(wt.getState()); // D.TERMINATED
    }
}
