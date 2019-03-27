package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Pool<T> {
	private int size;
	private List<T> items = new ArrayList<>();
	private volatile boolean[] checkout;
	// Using a semaphore inside a pool
	// to restrict the number of tasks that can use a resource;
	private Semaphore available;
	
	public Pool(Class<T> classObj, int size) {
		this.size = size;
		checkout = new boolean[size];
		available = new Semaphore(size,true);
		for(int i = 0; i < size; i++) {
			try {
				items.add(classObj.newInstance());
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * <p>从对象池中获取对象
	 * 
	 * @return
	 */
	private synchronized T getItem() {
		for(int i = 0; i < size; i++) {
			if(!checkout[i]) {
				checkout[i] = true;
				return items.get(i);
			}
		}
		return null;
	}
	
	/**
	 * <p>将对象释放回池中
	 * 
	 * @param t
	 * @return
	 */
	private synchronized boolean releaseItem(T t) {
		int index = items.indexOf(t);
		// if(index == -1) return false;
		if(index != -1 && checkout[index]) {
			checkout[index] = false;
			return true;
		}
		return false;
	}
	
	// 当池中没有对象时,阻塞调用过程
	// (没有任何信号许可量可用)
	public T checkOut() throws InterruptedException {
		available.acquire();
		return getItem();
	}
	
	// 当释放的对象有效,则向信号许可量返回一个许可证
	public void checkIn(T x) {
		if(releaseItem(x))
			available.release();
	}
}

// 模拟一个创建时极耗费资源的对象
@Slf4j
class Fat {
	private volatile double d;
	private static int count = 0;
	private final int id = count++;
	public Fat() {
		for(int i = 1; i < 10000 ; i++)
			d += (Math.PI+Math.E) / (double) i;
	}
	public void opt() {
		log.info("[{}],d:{}",this,d);
	}
	public String toString() {
		return "Fat_"+id;
	}
}

@Slf4j
class CheckOutTask<T> implements Runnable {
	private static int counter = 0;
	private final int id = counter++;
	private Pool<T> pool;
	public CheckOutTask(Pool<T> pool) {
		this.pool = pool;
	}
	@Override
	public void run() {
		try {
			T item = pool.checkOut();
			log.info("{} checkout {}",this,item);
			TimeUnit.SECONDS.sleep(1);
			log.info("{} checkin {}",this,item);
			pool.checkIn(item);
		} catch (InterruptedException e) {
			log.info("{} interrupted!",this);
		} catch (Exception e) {
			log.error("Exception : {}",e.toString());
		}
	}
	public String toString() {
		return "Checkout_task_"+id;
	}
}

/**
 * Semaphore锁Demo
 * 
 * @see java.util.concurrent.Semaphore
 * @author dk
 *
 */
@Slf4j
public class SemaphoreDemo {

	final static int SIZE = 25;
	
	@Test
	public void test() throws InterruptedException {
		final Pool<Fat> pool = new Pool<Fat>(Fat.class, SIZE);
		ExecutorService exec = Executors.newCachedThreadPool();
		
		for(int i = 0; i < SIZE; i++)
			exec.execute(new CheckOutTask<Fat>(pool));
		
		log.info("== All CheckoutTasks created! ==");

		List<Fat> list = new ArrayList<>();
		
		for(int i = 0; i < SIZE; i++) {
			Fat f = pool.checkOut();
			log.info("{}, main Thread checkout {}",i,f);
			f.opt();
			list.add(f);
		}
		
		Future<?> blocked = exec.submit(()->{
			// Semaphore prevents additional checkout
			// call is blocked;/
			return pool.checkOut();
		});
		
		TimeUnit.SECONDS.sleep(2);
		blocked.cancel(true);
		log.info("Checking in objects in {}",list);
		
		for(Fat f : list) pool.checkIn(f);
		
		for(Fat f : list) pool.checkIn(f);
		
		TimeUnit.SECONDS.sleep(9);
		exec.shutdownNow();
		
	}
	
}
