package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

class Count {
	private int count = 0;
	private Random random = new Random();
	// 使用sychronize控制
	public synchronized int increment(int incrementNumber) {
		int temp = incrementNumber;
		// 如果不加,即使不用synchronized,总数和各线程entrance和也有可能一致;
		if(random.nextBoolean()) Thread.yield();
		return (count += temp);
	}
	public synchronized int value() {return count;}
}

class Entrance implements Runnable {
	private static Count count = new Count();
	private Random random = new Random();
	private static List<Entrance> entrances = new ArrayList<>();
	// 线程本地number
	private int number = 0;
	private final int id;
	// 原子变量
	private static volatile boolean canceled = false;
	// 原子操作
	public static void cancel() {canceled = true;}
	public Entrance (int id) { 
		this.id = id;
		entrances.add(this);
	}
	public void run() {
		while(!canceled) {
			synchronized (this) {
				// 使用随机数,保证单个线程增加的数量不会与其他线程一致;
				int incrementNumber = 1 + random.nextInt(3);
				number += incrementNumber;
				System.out.println(this+"_Total_"+count.increment(incrementNumber));
			}
			OrnamentalGarden.sleep(100);
		}
		System.out.println("Stoping_"+this);
	}
	public synchronized int getValue() {return number;}
	public String toString() {
		return "Entrance_"+id+"_"+getValue();
	}
	public static int getTotalCount() {
		return count.value();
	}
	public static int sumEntrances() {
		int sum = 0;
		for(Entrance entrance : entrances) 
			sum += entrance.getValue();
		return sum;
	}
}

/**
 * <p>线程本地变量和公共变量的同步
 *
 */
public class OrnamentalGarden {

	public static void sleep(int mills) {
		try {
			TimeUnit.MILLISECONDS.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws InterruptedException {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i < 6; i++)
			exec.execute(new Entrance(i));
		sleep(1000);
		Entrance.cancel();
		exec.shutdown();
		if(!exec.awaitTermination(250, TimeUnit.MILLISECONDS))
			System.out.println("Some tasks were not terminated!");
		System.out.println("...Total_"+Entrance.getTotalCount());
		System.out.println("...Sum_of_Entrances_"+Entrance.sumEntrances());
	}
}
