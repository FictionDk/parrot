package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;


class DoThread implements Callable<String> {
    private String name;
    public DoThread(String name) {
        this.name = name;
    }
    @Override
    public String call() throws Exception {
        String msg = Thread.currentThread().getName()+"<"+name+">";
        System.out.println(">>"+Thread.currentThread().getId()+"|"+msg+"Doing");
        randomException();
        TimeUnit.SECONDS.sleep(1);
        return msg+" been finished";
    }
    
    private void randomException() {
        int i = new Random().nextInt(10);
        if(i > 4) throw new RuntimeException(name + " : Test Excption!");
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "DoThread [name=" + name + "]";
    }
}

@Slf4j
public class ThreadCallBackDemo {
    
    @Test
    public void test() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<DoThread> things = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            things.add(new DoThread("T-"+i));
        }
        
        try {
            List<Future<String>> results = pool.invokeAll(things);
            for(Future<String> re : results ) {
                try {
                    log.info("re: {}",re.get());
                }catch(Exception e) {
                    log.error("{}",e.toString());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
