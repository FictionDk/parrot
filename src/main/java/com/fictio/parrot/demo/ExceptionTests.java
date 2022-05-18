package com.fictio.parrot.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExceptionTests {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for(int i = 0; i < 10; i++){
            final long j = i;
            long tid = Thread.currentThread().getId();
            exec.execute(()-> System.out.printf("T=%d,i=%d,get=%d \r\n",tid,j,getValue(10+j,j)));
        }
        System.out.printf("T=%s,End,result=%s",exec.isTerminated(),exec.awaitTermination(3, TimeUnit.SECONDS));
        exec.shutdown();
    }

    static long getValue(Long start, Long end){
        long value = 0;
        try{
            value = start / end;
            return value;
        }catch (Exception e){
            throw new RuntimeException(start+"/"+end+" get value err:"+e.getMessage());
        }finally {
            publish(String.format("T=%d,Start=%d,End=%d,Value=%d",Thread.currentThread().getId(),start,end,value));
        }
    }

    static void publish(String record){
        System.out.println(record);
    }
}
