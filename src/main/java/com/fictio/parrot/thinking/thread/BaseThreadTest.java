package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class LiftOff implements Runnable {
	
	protected int countDown = 10;
	private static int taskCount = 0;
	private final int id = taskCount ++;
	public LiftOff() {
	}
	public LiftOff(int countDown) {
		this.countDown = countDown;
	}
	public static void initId() {
		taskCount = 0;
	}
	public String status() {
		return "#"+id+"("+
				(countDown > 0 ? countDown : "Liftoff!")+"),";
	}
	@Override
	public void run() {
		while(countDown-- > 0) {
			log.info("status :{}",status());
			//让步.暗示(但并不保证会被采纳)jvm可以让别的线程使用cpu
			Thread.yield();
		}
	}
}

class TaskWithResult implements Callable<String> {
	private int id;
	public TaskWithResult(int id) {
		this.id = id;
	}
	@Override
	public String call() throws Exception {
		return "result of TaskWithResult "+id;
	}
}

@Slf4j
class SleepingTask extends LiftOff {
	public void run() {
		try {
			while(countDown-- > 0) {
				log.info("status :{}",status());
				TimeUnit.MILLISECONDS.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


/**
 * Thinking in Java. 21并发
 * 
 * @author dk
 *
 */
@Slf4j
public class BaseThreadTest {

	//21.2.1 Runable
	@Test
	public void liftTest() {
		LiftOff launch = new LiftOff(13);
		launch.run();
	}
	
	//22.2.2 Thread
	@Test
	public void threadTest() {
		Thread t = new Thread(new LiftOff());
		t.start();
		log.info("waiting for liftoff");
	}
	
	// 确保测试完成前所有线程都能结束
	public void confireWatingForEnd() {
		log.info("waiting for liftoff");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//22.2.2 Thread
	@Test
	public void theadsTest() {
		for(int i = 0; i < 3; i++)
			new Thread(new LiftOff()).start();
		confireWatingForEnd();
	}
	
	//22.2.3 Exector
	@Test
	public void threadPoolTest() {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0 ; i < 5; i++)
			exec.execute(new LiftOff());
		exec.shutdown();
		confireWatingForEnd();
		
		log.info("=======================");
		LiftOff.initId();
		
		exec = Executors.newFixedThreadPool(5);
		for(int i = 0; i < 5; i++)
			exec.execute(new LiftOff());
		exec.shutdown();
		confireWatingForEnd();
		
	}

	@Test
	public void callableTest() {
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<String>> results = new ArrayList<>();
		for(int i = 0; i < 20; i++) 
			results.add(exec.submit(new TaskWithResult(i)));
		
		for(Future<String> result : results) {
			try {
				log.info("fs.get():{}",result.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				exec.shutdown();
			}
		}
	}
	
	@Test
	public void sleepTest() {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < 5; i++) {
			exec.execute(new SleepingTask());
		}
		exec.shutdown();
		confireWatingForEnd();
	}
	
	// 后台守护进程Thread.setDaemon(true)
	@Test
	public void daemonTest() {
	}
}
