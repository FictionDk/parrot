package com.fictio.parrot.logic.thread;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

interface Callable<V> {
    V call() throws Exception;
}

interface MyFuture<V> {
    V get() throws Exception;
}

class MyExecuteThread<V> implements Runnable {

    private V result = null;
    private Exception ex = null;
    private boolean done = false;
    private Callable<V> task;
    private Object lock;
    public MyExecuteThread(Callable<V> task, Object lock) {
        this.task = task;
        this.lock = lock;
    }
    @Override
    public void run() {
        try {
            this.result = task.call();
        } catch (Exception e) {
            this.ex = e;
        } finally {
            synchronized (lock) {
                System.out.println("ExecuteThread,Get Result = " + result);
                done = true;
                lock.notifyAll();
            }
        }
    }
    public V getResult() {
        return result;
    }
    public boolean isDone() {
        return done;
    }
    public Exception getException() {
        return ex;
    }
}

class MyExecute {
    public <V> MyFuture<V> execute(final Callable<V> task){
        final Object lock = new Object();
        final MyExecuteThread<V> thread = new MyExecuteThread<>(task, lock);
        new Thread(thread, "TASK").start();
        MyFuture<V> future = new MyFuture<V>() {
            @Override
            public V get() throws Exception {
                synchronized (lock) {
                    while (!thread.isDone()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(thread.getException() != null) throw thread.getException();
                    return thread.getResult();
                }
            }
        };
        return future;
    }
}


public class FeatureCallDemo {
    @Test public void tests() throws Exception {
        Callable<Integer> subTask = new Callable<Integer>() {
            @Override
            public Integer call() throws InterruptedException {
                int millis = (int) (Math.random() * 1000);
                Thread.sleep(millis);
                System.out.println("Call: " + millis);
                return millis;
            }
        };
        MyFuture<Integer> future = new MyExecute().execute(subTask);
        Integer result = future.get();
        System.out.println(result);
        TimeUnit.SECONDS.sleep(2);
    }
}
