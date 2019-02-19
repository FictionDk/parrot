package com.fictio.parrot.thinking.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

abstract class IntGenerator {
	private volatile boolean canceled = false;
	public abstract int next();
	public void cancel() { canceled = true; }
	public boolean isCanceled() { return canceled; }
}

@Slf4j
class EvenChecker implements Runnable {
	private IntGenerator generator;
	@SuppressWarnings("unused")
	private final int id;
	public EvenChecker (IntGenerator g, int ident) {
		generator = g;
		id = ident;
	}
	public void run() {
		while (!generator.isCanceled()) {
			// 多个线程同时操作generator.next()
			int val = generator.next();
			if(val % 2 != 0) {
				log.info("{} not even !",val);
				generator.cancel();
			}else {
				log.info("{} go",val);
			}
		}
	}
	
	public static void test(IntGenerator gp, int count) {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < count; i++) 
			exec.execute(new EvenChecker(gp, count));
		exec.shutdown();
	}
	
	public static void test(IntGenerator gp) {
		test(gp,10);
	}
}

/**
 * 并发资源访问Demo
 * 基本所有并发模型在解决线程冲突问题时,都采用的是序列化访问共享资源
 *   -- 给定时刻,只允许一个任务访问共享资源
 * 通常是通过在代码前加锁语句来实现(使得在一段时间内只有一个任务可以运行这段代码)
 * 因为锁语句产生了一种互相排斥的效果,所有这种机制被称为: 互斥量(mutex)
 * 
 * Java使用synchronized关键字保护代码片段
 * 
 * 共享资源一般以对象形式存在内存片段,也可以是文件,输入/输出流,打印机;
 * 要想控制对共享资源的访问,先把它包装进一个对象;然后把所有要访问这个资源的方法标记为synchronized
 * 
 * 注: 使用并发时,将域设为private可以确保其他任务可以直接访问;
 * 
 * 锁计数: JVM负责跟踪对象被加锁的次数:
 * 	- 当任务第一次给对象加锁的时候,计数变为1
 *  - 每当这个相同的任务在这个对象获得锁,计数递增(即第一个获得锁的任务才有可能递增)
 *  - 每当任务离开一个synchronized方法,计数递减
 *  - 计数为0时,锁释放
 *  
 *  针对每一个类,也有一个锁,所以synchronized static方法可以在类的范围内防止对static数据并发访问
 *
 *  Braian的同步规则:
 *   - 如果一个变量接下来将被另外一个线程读取,或者正在读取一个上一次已经被另一个线程写过的变量,则
 *    必须使用同步,且读写线程用相同的监视器锁同步
 */
public class ResouresThreadDemo {
	
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

	/**
	 * 使用多个线程无限制的生产偶数
	 * pool-1-thread-5 val: 557740
	 * pool-1-thread-5 val: 557742
	 * pool-1-thread-8 val: 557312
	 * pool-1-thread-8 val: 557346
	 */
	private class EvenGenerator extends IntGenerator {
		private int currentEvenValue = 0;
		public int next() {
			++currentEvenValue;
			Thread.yield();
			++currentEvenValue;
			return currentEvenValue;
		}
		@SuppressWarnings("unused")
		public void test() {
			EvenChecker.test(new EvenGenerator());
			mainThreadSleep(10);
		}
	}
	
	@Test
	public void evenGeneratorTest() {
		EvenChecker.test(new EvenGenerator());
		mainThreadSleep(10);
	}
	

}
