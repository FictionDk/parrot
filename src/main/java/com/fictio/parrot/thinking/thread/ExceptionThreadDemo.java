package com.fictio.parrot.thinking.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class ExcetionThread implements Runnable {
	public void run() {
		throw new RuntimeException("new Exception!");
	}
}

@Slf4j
class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.info("{} caught {}",t,e.toString());
	}
}

@Slf4j
class HandlerThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(Runnable r) {
		log.info("{} creating new Thread.",this.getClass().getSimpleName());
		Thread t = new Thread(r);
		log.info("created {}",t);
		t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		return t;
	}
}

@Slf4j
public class ExceptionThreadDemo {

	/**
	 * 不能捕获线程中逃逸的异常
	 */
	@Test
	public void exceptionTest() {
		try {
			ExecutorService exec = Executors.newCachedThreadPool();
			exec.execute(new ExcetionThread());
		}catch(Exception e) {
			log.info("get Exception {}",e);
		}	}
	
	/**
	 * 使用自定义的线程工厂HandlerThreadFactory创建特定线程
	 * t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	 * 使得每个新建的Thread对象上附着一个UncaughtExceptionHandler,用来捕获线程
	 */
	@Test
	public void exceptionCaught() {
		ExecutorService exec = Executors.newCachedThreadPool(new HandlerThreadFactory());
		exec.execute(new ExcetionThread());
		
		new BaseThreadTest().confireWatingForEnd();
	}
	
	/**
	 * Thread类中设置一个静态域
	 */
	@Test
	public void execptionDefaultHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new ExcetionThread());
		
		new BaseThreadTest().confireWatingForEnd();
	}

}