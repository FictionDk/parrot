package com.fictio.parrot.logic.thread;

import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

class MyBlockingQueue<E> {
    private Queue<E> queue = null;
    private int limit;

    public MyBlockingQueue(int limit) {
        this.limit = limit;
        this.queue = new ArrayDeque<>(limit); // 非线程安全
    }

    public synchronized void put(E e) throws InterruptedException {
        while (queue.size() == limit) wait();
        queue.add(e);
        notifyAll();  // 变量被synchronized保护,如果不在synchronized内调用wait/notify时会报错
    }

    public synchronized E take() throws InterruptedException {
        while (queue.size() == 0) wait();
        E e = queue.poll();
        notifyAll();
        return e;
    }
}

class Producer implements Runnable {
    MyBlockingQueue<String> queue;
    public Producer(MyBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int num = 0;
        while(true) {
            try {
                queue.put(Thread.currentThread().getName()+"_"+num);
                num++;
                Thread.sleep((int) Math.random()*100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {
    MyBlockingQueue<String> queue;
    public Consumer(MyBlockingQueue<String> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        while(true) {
            try {
                System.out.println(Thread.currentThread().getName()+" get: "+queue.take());
                Thread.sleep((int) Math.random()*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * @Desc
 *
 */
public class ProducerAndConsumerDemo {

    @Test public void tests() {
        MyBlockingQueue<String> queue = new MyBlockingQueue<>(10);
        Producer p = new Producer(queue);
        Consumer c = new Consumer(queue);

        new Thread(p,"P1").start();
        new Thread(p,"P2").start();
        new Thread(c,"C1").start();

        DeadLockDemo.doSleep(5);
    }

}
