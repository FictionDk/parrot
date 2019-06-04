package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

abstract class Teston {
    int id;
    String name;
    public String toString() {
        return this.id+"|"+this.name;
    }
}

class Singleton extends Teston {
    
    private static class SingletonHolder {
        private static Singleton instance = new Singleton();
    }
    public static Teston build() {
        return SingletonHolder.instance;
    }
    private Singleton() {}
}

class Normalon extends Teston {
    private Normalon() {}
    public static Teston build() {
        return new Normalon();
    }
}

enum ClassType {
    SINGLET,NORMAL
}

class TestonFactory {
    public static Teston build(ClassType type) {
        switch (type) {
        case SINGLET:
            return Singleton.build();
        case NORMAL:
            return Normalon.build();
        default:
            return null;
        }
    }
}

/**
 * <p> 单例 对象线程安全性测试
 *
 */
@Slf4j
public class SingleThreadDemo {

    private void mainThreadSleep(long seconds) {
        try {
            if(seconds == 0)
                TimeUnit.MILLISECONDS.sleep(8);
            else
                TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private synchronized Teston objReset() {
        Teston obj = TestonFactory.build(ClassType.NORMAL);
        obj.id = 10000 + new Random().nextInt(89998);
        obj.name = UUID.randomUUID().toString();
        return obj;
    }
    
    @Test
    public void test() {
        ExecutorService exec = Executors.newCachedThreadPool();
        
        for(int i = 0; i < 2000; i++) {
            exec.execute(()->{
                Teston obj = objReset();
                log.info("Teston Set : {}",obj);
                Thread.yield();
            });
        }
        for(int i = 0; i < 2000; i++) {
            exec.execute(()->{
                Teston obj = TestonFactory.build(ClassType.NORMAL);
                log.info("Teston Get: {}",obj);
                Thread.yield();
            });
        }
        
        mainThreadSleep(20);
    }
    
}
