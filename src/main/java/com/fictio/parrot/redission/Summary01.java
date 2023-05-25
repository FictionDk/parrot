package com.fictio.parrot.redission;

import io.reactivex.rxjava3.core.Flowable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Summary01 {

    @Test
    public void rxJava3(){
        Flowable.range(0,20).subscribe(new Subscriber<Integer>() {
            static final int BACK_NUM = 3;
            Subscription s;
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("SubStarted");
                s = subscription;
                s.request(BACK_NUM);
                log.info("SubEnded");
            }
            @Override
            public void onNext(Integer integer) {
                log.info("OnNext={}",integer);
                s.request(BACK_NUM);
            }
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
            @Override
            public void onComplete() {
                log.info("SubCompleted");
            }
        });

        Flowable<Integer> flowable = Flowable.generate(()-> 0,(s,emitter)->{
            int nextVal = s + 1;
            if(nextVal > 20) emitter.onComplete();
            else emitter.onNext(nextVal);
            return nextVal;
        });
        log.info("{}",flowable.subscribe(val->log.info("{}",val)));
    }

    @SneakyThrows
    @Test
    public void test(){
        Config config = new Config();
        config.useSingleServer().setConnectionPoolSize(1).setConnectionMinimumIdleSize(1)
                .setAddress("redis://192.168.110.13:16379")
                .setPassword("st123").setTimeout(3000).setDatabase(0);
        RedissonClient client = Redisson.create(config);
        RLock lock = client.getLock("myLock");
        ExecutorService exec = Executors.newCachedThreadPool();
        AtomicInteger atoVal = new AtomicInteger(10);
        List<Integer> norValList = new ArrayList<>(Collections.singletonList(10));
        exec.execute(()-> run(lock, atoVal, norValList));
        exec.execute(()-> run(lock, atoVal, norValList));
        TimeUnit.SECONDS.sleep(15);
        log.info("Result={},{}",atoVal.get(),norValList.get(0));
    }

    private void run(RLock lock, AtomicInteger val, List<Integer> norValList){
        String tName = Thread.currentThread().getName();
        try {
            // 等待10s取锁, 4s自动释放锁；如果是自动释放锁,原线程会继续,并发问题依然存在
            if(lock.tryLock(10,4, TimeUnit.SECONDS)){
                Integer norVal = norValList.get(0);
                log.info("GetLock{}",tName);
                TimeUnit.SECONDS.sleep(6);
                // addAndGet-compareAndSwapInt 自旋锁
                log.info("Val{}={},{}",tName,val.addAndGet(1),++norVal);
                norValList.set(0, norVal);
            }else{
                log.info("GetLog{},Err",tName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if(lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
                log.info("UnLock{}",tName);
            }else {
                log.info("AutoUnLock{}",tName);
            }
        }
    }


}
