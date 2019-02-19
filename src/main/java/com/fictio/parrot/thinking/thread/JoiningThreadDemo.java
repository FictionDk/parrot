package com.fictio.parrot.thinking.thread;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Sleeper extends Thread {
	private int duration;
	public Sleeper(String name,int sleepTime) {
		super(name);
		duration = sleepTime;
		start();
	}
	public void run() {
		log.info("{} get start",getName());
		try {
			sleep(duration);
		} catch (InterruptedException e) {
			// 当线程在sleep过程中,被调用interrupt(),标记线程被中断
			// 但异常被捕获时,将清理这个标志,所以标记isInterrupted()为False
			log.info("{} was interrupted. isInterrupted(): {}",getName(),isInterrupted());
			return;
		}
		log.info("{} has awakened",getName());
	}
}

@Slf4j
class Joiner extends Thread {
	private Sleeper sleeper;
	public Joiner (String name, Sleeper sleeper) {
		super(name);
		this.sleeper = sleeper;
		start();
	}
	public void run() {
		log.info("{} get start",getName());
		try {
			/**
			 * 一个线程可以在其他线程之上调用join()
			 * 效果是等待一段时间直到第二个线程结束才继续执行
			 * 也可以调用
			 * 	public final synchronized void join(long millis, int nanos);
			 */
			sleeper.join();
		} catch (InterruptedException e) {
			log.info("#{}# was interrupted.",getName());
		}
		log.info("{} join completed",getName());
	}
}

public class JoiningThreadDemo {
	
	@Test
	public void test() {
		Sleeper sleepy = new Sleeper("Sleepy", 1500);
		Sleeper grumpy = new Sleeper("Grumpy", 1500);
		
		new Joiner("Dopey", sleepy);
		new Joiner("Doc", grumpy);
		
		/**
		 * OUTPUT:
		 * Sleepy was interrupted. isInterrupted(): false
		 * Dopey join completed
		 * Grumpy has awakened
		 * Doc join completed
		 * 
		 * Sleeper被中断或者是正常结束,Joiner将和Sleeper一同结束
		 */
		//grumpy.interrupt();
		sleepy.interrupt();
		
		new BaseThreadTest().confireWatingForEnd();
	}

}
