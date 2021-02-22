package com.fictio.parrot.logic.thread;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class JoinThread extends Thread {
    public JoinThread(String name) {
        super(name);
    }
    @Override
    public void run() {
        System.out.println("-----"+this.getId()+" run start----------");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.getId()+":"+this.getName());
        System.out.println("alive: "+this.isAlive());
        System.out.println("daemon: "+this.isDaemon());
        System.out.println("---------"+this.getId()+" run end----------");
    }

    public String info() {
        return new StringBuilder(String.valueOf(this.getId()))
                .append(":").append(this.getName()).append(",")
                .append("alive:").append(this.isAlive()).append(",")
                .append("daemon:").append(this.isDaemon()).toString();
    }
}

@Slf4j
public class BaseThreadDemo {

    private void threadJoinTest() throws InterruptedException {
        JoinThread joinA = new JoinThread("A");
        JoinThread joinB = new JoinThread("B");
        log.debug("{}",joinA.info());
        log.debug("{}",joinB.info());
        joinA.start();
        joinB.start();
        joinB.join();
        //joinA.join();
    }

    @Test public void test() throws InterruptedException {
        threadJoinTest();  // 使用@Test调用时,join 发挥了作用
    }

    public static void main(String[] args) throws InterruptedException {
        //BaseThreadDemo demo = new BaseThreadDemo();
        //demo.threadJoinTest();  // 使用main方法时,join 没有发挥作用,不使用join依旧阻塞直至2线程全部完成
        SharedMemoryDemo memoryDemo = new SharedMemoryDemo();
        memoryDemo.test();
        memoryDemo.visibiltyTest();

        DeadLockDemo lockDemo = new DeadLockDemo();
        lockDemo.lockTest();
    }
}
