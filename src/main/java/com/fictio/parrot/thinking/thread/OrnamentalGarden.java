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
	public synchronized int increment() {
		int temp = count;
		if(random.nextBoolean()) Thread.yield();
		return (count = ++temp);
	}
	public synchronized int value() {return count;}
}

class Entrance implements Runnable {
	private static Count count = new Count();
	private static List<Entrance> entrances = new ArrayList<>();
	private int number = 0;
	private final int id;
	private static volatile boolean canceled = false;
	public static void cancel() {canceled = true;}
	public Entrance (int id) { 
		this.id = id;
		entrances.add(this);
	}
	public void run() {
		while(!canceled) {
			synchronized (this) {
				++number;
			}
			System.out.println(this+"_Total_"+count.increment());
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
		sleep(3000);
		Entrance.cancel();
		exec.shutdown();
		if(!exec.awaitTermination(250, TimeUnit.MILLISECONDS))
			System.out.println("Some tasks were not terminated!");
		System.out.println("...Total_"+Entrance.getTotalCount());
		System.out.println("...Sum_of_Entrances_"+Entrance.sumEntrances());
	}
}
