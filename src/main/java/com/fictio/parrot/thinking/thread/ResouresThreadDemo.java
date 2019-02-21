package com.fictio.parrot.thinking.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

// 生产序列化整数
class SerialNumberGenerator {
	// 确保一个线程写操作后(立即写入主存中),其他线程域的读操作都看到这个修改
	private static volatile int serialNumber = 0;
	public static int nextSerialNumber() {
		// 非原子操作,且未加锁,线程不安全
		return serialNumber++;
	}
}

// 有限数组集合
class CircularSet {
	private int[] arr;
	private int len;
	private int index;
	public CircularSet(int size) {
		arr = new int[size];
		len = size;
		for(int i = 0; i < size; i++) {
			arr[i] = -1;
		}
	}
	public synchronized void add(int i) {
		arr[index] = i;
		// Wrap index and write over old ele
		index = ++index % len;
	}
	public synchronized boolean contains(int val) {
		for(int i = 0; i < len; i++)
			if(arr[i] ==  val) return true;
		return false;
	}
}

@Slf4j
class SerialNumberChecker {
	private static final int SIZE = 10;
	private static CircularSet serials = new CircularSet(1000);
	
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	/**
	 * <p>1)采用多个线程使用SerialNumberGenerator生成序列化整数
	 * <p>2)多个线程同时往数据集CircularSet.arr中插入数据
	 * <p>3)检查数据,如果发现重复,程序退出运行
	 * <p>4)如果重复,证明即使使用volatile关键字,serialNumber++操作不安全
	 */
	static class SerialCheaker implements Runnable {
		public void run() {
			while(true) {
				int serial = SerialNumberGenerator.nextSerialNumber();
				if(serials.contains(serial)) {
					log.info("Duplicate : {}",serial);
					System.exit(0);
				}
				serials.add(serial);
			}
		}
	}
	
	public static void serialNumberTest() {
		for(int i = 0; i < SIZE; i++) {
			exec.execute(new SerialCheaker());
		}
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
@Slf4j
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
	
	/**
	 * 使用synchronized关键字确保
	 */
	private class SyncEvenGenerator extends IntGenerator {
		private int currentEvenValue = 0;
		public synchronized int next() {
			++currentEvenValue;
			Thread.yield();
			++currentEvenValue;
			return currentEvenValue;			
		}
	}
	
	@Test
	public void syncEvenGeneratorTest() {
		EvenChecker.test(new SyncEvenGenerator());
		mainThreadSleep(10);		
	}
	
	/**
	 * 使用: ReentrantLock
	 * 相对于synchronized关键字(失败抛出异常,无法清理/善后或者说是状态回滚)
	 */
	private class MutexEvenGenerator extends IntGenerator {
		private int currentEvenValue = 0;
		private Lock lock = new ReentrantLock();
		public int next() {
			lock.lock();
			try {
				++currentEvenValue;
				Thread.yield();
				++currentEvenValue;
				return currentEvenValue;
			} finally {
				lock.unlock();
			}
		}
	}
	
	@Test
	public void mutexEvenGeneratorTest() {
		EvenChecker.test(new MutexEvenGenerator());
		mainThreadSleep(10);		
	}
	
	private class AttemptLocking {
		// 允许尝试获取锁但最终未获取锁
		private ReentrantLock lock = new ReentrantLock();
		public void untimed() {
			boolean captured = lock.tryLock();
			// 如果拿到了锁打印日志
			try {
				log.info("tryLock(): {}",captured);
			}finally {
				if(captured) lock.unlock();
			}
		}
		public void timed() {
			boolean captured = false;
			try {
				captured = lock.tryLock(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				log.info("trylock(2,TimeUnit.SECONDS): {}",captured);
			}finally {
				if(captured) lock.unlock();
			}
		}
	}
	
	@Test
	public void attempLockTest() {
		final AttemptLocking al = new AttemptLocking();
		al.untimed();
		al.timed();
		
		new Thread() {
			{setDaemon(true);}
			public void run() {
				al.lock.lock();
				log.info("acquired");
			}
		}.start();
		
		Thread.yield();
		
		al.untimed();
		al.timed();
		
		mainThreadSleep(1);
	}
	
	@Test
	public void serialNumberTest() {
		SerialNumberChecker.serialNumberTest();
		mainThreadSleep(10);
		log.info("No duplicate detecet");
	}
	
	/**
	 * <p> 使用特殊原子性变量类,提供原子性条件更新
	 *
	 */
	private class AtomicEvenGenerator extends IntGenerator {
		private AtomicInteger currentEvenValue = new AtomicInteger(0);
		public int next() {
			return currentEvenValue.addAndGet(2);
		}
	}
	
	@Test
	public void atomicEvenGenratorTest() {
		EvenChecker.test(new AtomicEvenGenerator());
		mainThreadSleep(2);
	}

}
