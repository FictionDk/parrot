package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TaskPortion implements Runnable {
	private static int counter = 0;
	private final int id = counter ++;
	private static Random rand = new Random(51);
	private final CountDownLatch latch;
	public TaskPortion(CountDownLatch latch) {
		this.latch = latch;
	}
	@Override
	public void run() {
		try {
			doWork();
			/**
			 * Decrements the count of the latch, releasing all waiting threads if
			 * the count reaches zero.
			 * 减少门闩的数量,当值减到0时释放所有等待线程
			 *
			 * <p>If the current count is greater than zero then it is decremented.
			 * If the new count is zero then all waiting threads are re-enabled for
			 * thread scheduling purposes.
			 * 如果当前计数大于零，则递减。
			 * 如果新计数为零，则重新启用所有等待线程以进行线程调度。
			 *
			 * <p>If the current count equals zero then nothing happens.
			 */
			latch.countDown();
		} catch (Exception e) {
			log.error("taskPortion_{} interruption",id);
		}
	}
	private void doWork() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(rand.nextInt(200));
		log.info("{},complated!",id);
	}
	public String toString() {
		return String.format("%1$-3d", id);
	}
}

@Slf4j
class WaitingTask implements Runnable {
	private static int counter = 0;
	private final int id = counter ++;
	private final CountDownLatch latch;
	public WaitingTask(CountDownLatch latch) {
		this.latch = latch;
	}
	public void run() {
		try {
			Thread.yield();
			log.info("Latch barrier waiting for {}",this);
			latch.await();
			log.info("Latch barrier passed for {}",this);
		} catch (Exception e) {
			log.error("waiting Task_{} interruption,{}",this,e.toString());
		}
	}
	public String toString() {
		return "WaitingTask_"+id;
	}
}

/**
 * <p> CountDownLatch
 *
 */
@Slf4j
public class CountDownLatchDemo {
	
	static final int SIZE = 100;
	
	/**
	 * <p> 当所有TaskPortion将CountDownLatch.count降低为0时,WaitingTask任务开始运行;
	 * <p> CountDownLatch.count没有到0时.WaitingTask会一直在latch.await()等待;
	 */
	@Test
	public void test() {
		ExecutorService exec = Executors.newCachedThreadPool();
		
		CountDownLatch latch = new CountDownLatch(SIZE);
		
		for(int i = 0; i < SIZE; i++)
			exec.execute(new WaitingTask(latch));
		
		for(int i = 0; i < SIZE; i++)
			exec.execute(new TaskPortion(latch));
		
		log.info("Launched all tasks ...");
		OrnamentalGarden.sleep(4000);
		exec.shutdownNow();
	}

}
