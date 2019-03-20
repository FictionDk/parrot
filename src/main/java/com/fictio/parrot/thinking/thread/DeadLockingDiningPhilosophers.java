package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Chopstick {
	private boolean taken = false;
	// 拿筷子
	public synchronized void take() throws InterruptedException {
		while(taken) wait();
		taken = true;
	}
	// 放筷子
	public synchronized void drop() {
		taken = false;
		notifyAll();
	}
}

@Slf4j
class Philosopher implements Runnable {
	// 左右的筷子(资源)分别由其他哲学家共享
	private Chopstick left;
	private Chopstick right;
	private final int id;
	// 调解因子,哲学家思考的时长
	private final int ponderFactor;
	private Random rand = new Random(50);
	public Philosopher(Chopstick left,Chopstick right,int id,int ponderFactor) {
		this.left = left;
		this.right = right;
		this.id = id;
		this.ponderFactor = ponderFactor;
	}
	private void pause() throws InterruptedException {
		if(ponderFactor == 0 ) return;
		TimeUnit.MILLISECONDS.sleep(rand.nextInt(ponderFactor * 250));
	}
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				log.info("{} thinking ...",this);
				pause();
				log.info("{} grabbing left",this);
				left.take();
				log.info("{} grabbing right",this);
				right.take();
				log.info("{} eating ...",this);
				pause();
				right.drop();
				left.drop();
			}
		} catch (Exception e) {
			log.error("{} exiting via intterupption",this);
		}
	}
	public String toString() {
		return "Philosopher_"+id;
	}
}


/**
 * <p> 死锁--哲学家就餐问题
 * 
 * <p> 死锁的条件(同时满足)
 * <l> 1) 互斥,存在资源不能共享(有竞争) -- 筷子某一时刻只能被一个哲学家持有;
 * <l> 2) 至少有资源持有一个资源同时等待另外一个资源 -- 哲学家拿了一只筷子后需要等待另外一只才能完成任务;
 * <l> 3) 资源不能被抢占
 * <l> 4) 有循环等待
 */
public class DeadLockingDiningPhilosophers {
	int size = 5;
	int ponder = 4;
	
	// 生产筷子
	private Chopstick[] buildSticks() {
		Chopstick[] sticks = new Chopstick[size];
		for(int i = 0; i < size; i++)
			sticks[i] = new Chopstick();
		return sticks;
	}

	@Test
	public void test() {
		Chopstick[] sticks = buildSticks();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < size; i++)
			exec.execute(new Philosopher(sticks[i], sticks[(i+1)/size], i, ponder));
		
		OrnamentalGarden.sleep(6000);
		exec.shutdown();
	}
	
	/**
	 * <p> 确定哲学家拿筷子的顺序
	 */
	@Test
	public void fixTest() {
		Chopstick[] sticks = buildSticks();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < size; i++)
			if(i < (size-1))
				exec.execute(new Philosopher(sticks[i], sticks[i+1], i, ponder));
			else
				exec.execute(new Philosopher(sticks[0], sticks[1], i, ponder));
		
		OrnamentalGarden.sleep(6000);
		exec.shutdown();
	}

}
