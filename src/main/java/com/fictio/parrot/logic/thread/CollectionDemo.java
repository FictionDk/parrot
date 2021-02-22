package com.fictio.parrot.logic.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> 普通增强型容器对象
 */
class EnhancedMap<K, V>{
    Map<K, V> map;
    public EnhancedMap(Map<K,V> map) {
        this.map = Collections.synchronizedMap(map); // 基于mutex的synchronized 对象锁
    }
    public V putIfAbsent(K key, V value) {
        V old = map.get(key);
        if(old != null) return old;
        return map.put(key, value);
    }
    public Map<K,V> getMap(){
        return this.map;
    }
}

/**
 * <p> 增加锁后的容器对象
 */
class SyncEnhancedMap<K, V> extends EnhancedMap<K, V>{
    public SyncEnhancedMap(Map<K, V> map) {
        super(map);
    }
    @Override
    public V putIfAbsent(K key, V value) {
        // synchronized {...} 伪同步,锁不一致
        synchronized (map) { // 不能直接方法上用this对象锁.因为Collections.synchronizedMap和SyncEnhancedMap对象不是一个锁
            V old = map.get(key);
            if(old != null) return old;
            return map.put(key, value);
        }
    }
}


@Slf4j
public class CollectionDemo {

    private int counter = 0;

    private synchronized int getCount() {
        return counter++;
    }

    @Test public void enhancedMapTest() {  // 01: 复合操作
        Map<String, Integer> raw = new HashMap<>();
        EnhancedMap<String, Integer> em = new EnhancedMap<>(raw);
        ExecutorService pool = Executors.newCachedThreadPool();
        for(int i = 0; i < 10; i++)
            pool.execute(()->{
                log.debug("c={}",em.putIfAbsent("A", getCount()));
            });
        log.debug("{}",raw);
        DeadLockDemo.doSleep(1);
    }

    @Test public void syncEnhancedMapTest() {  // 02: 伪同步
        Map<String, Integer> raw = new HashMap<>();
        EnhancedMap<String, Integer> em = new SyncEnhancedMap<>(raw);
        ExecutorService pool = Executors.newCachedThreadPool();
        for(int i = 0; i < 10; i++)
            pool.execute(()->{
                log.debug("c={}",em.putIfAbsent("A", getCount()));
            });
        log.debug("{}",raw);
        DeadLockDemo.doSleep(1);
    }

    @Test public void iteratorModifyTest() { // 03: 迭代
        final List<String> list = Collections.synchronizedList(new ArrayList<String>());

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 100; i++) {
                    list.add("item_"+i);
                    try {
                        TimeUnit.SECONDS.sleep(new Random().nextInt(2));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 如果没加锁,或加错锁(this): java.util.ConcurrentModificationException
                    synchronized(list) {
                        for(String str : list) System.out.println(str);;
                    }
                }
            }
        }).start();

        DeadLockDemo.doSleep(5);
    }
}
