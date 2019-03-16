package com.fictio.parrot.thinking.thread;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

class Car {
	private boolean waxOn = false;
	public synchronized void waxed() {
		waxOn = true;
		notifyAll();
	}
	public synchronized void buffed() {
		waxOn = false;
		notifyAll();
	}
	public synchronized void waitForWaxing()
	throws InterruptedException {
		while(waxOn == false) wait();
	}
	public synchronized void waitForBuffing() 
	throws InterruptedException{
		while(waxOn == true) wait();
	}
}

class WaxOn implements Runnable {
	private Car car;
	public WaxOn(Car car) {
		this.car = car;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				System.out.println("Wax On !");
				OrnamentalGarden.sleep(100);
				car.waxed();
				car.waitForBuffing();
			}
		} catch (InterruptedException e) {
			System.out.println("WaxOn,Exiting via interrupt !");
		}
		System.out.println("Ending wax on task !");
	}
}

class WaxOff implements Runnable {
	private Car car;
	public WaxOff(Car car) {
		this.car = car;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				car.waitForWaxing();
				System.out.println("Wax Off !");
				OrnamentalGarden.sleep(200);
				car.buffed();
			}
		} catch (Exception e) {
			System.out.println("WaxOff,Exiting via interrupt !");
		}
		System.out.println("Ending Wax Off task !");
	}
}

/**
 * <p>线程间的协作
 * 
 * @author dk
 *
 */
public class WaxOMatic {

	private abstract class Food {
		String name;
		public Food(String name) {
			this.name = name;
		}
	}
	
    class Cake extends Food {
		String builder;
		LocalDateTime buildTime;
		LocalDateTime packTime;
		LocalDateTime labelTime;
		public Cake(String builder) {
			super("cake");
			this.builder = builder;
			this.buildTime = LocalDateTime.now();
		}
		public synchronized void pack() {
			OrnamentalGarden.sleep(1000);
			this.packTime = LocalDateTime.now();
			notifyAll();
		}
		public synchronized void label() {
			this.labelTime = LocalDateTime.now();
			notifyAll();
		}
		public synchronized void waitingForPack() {
			if(packTime == null)
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Wating for pack failed!");
					e.printStackTrace();
				}
		}
		public synchronized void waitingForLabel() {
			if(labelTime == null)
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Wating for label failed!");
					e.printStackTrace();
				}
		}
		@Override
		public String toString() {
			StringBuilder builder2 = new StringBuilder();
			builder2.append("{builder:").append(builder).append(", buildTime:").append(buildTime).append(", packTime:")
					.append(packTime).append(", labelTime:").append(labelTime).append("}");
			return builder2.toString();
		}
	}
	
    // pack for cake
	private class Packer implements Runnable {
		private Cake cake;
		public Packer(Cake cake) {
			this.cake = cake;
		}
		public void run() {
			if(cake.packTime == null) {
				cake.pack();
				cake.waitingForLabel();
			}
		}
	}
	
	// label for cake,Cake need pack first
	private class Labeler implements Runnable {
		private Cake cake;
		public Labeler(Cake cake) {
			this.cake = cake;
		}
		@Override
		public void run() {
			if(cake.packTime == null) {
				cake.waitingForLabel();
			}else if(cake.packTime != null && cake.labelTime == null) {
				cake.label();
			}
			
		}
	}
	
	List<String> waiters = Arrays.asList("Tom","Joe","Bin","Air","Alica","Ginduluman",
			"Talibon","Tabilaran","Alon","Anda","Tibigon","Jao");
	
	private synchronized String getName(int id) {
		return waiters.get(id);
	}
	
	@Test
	public void test2() {
		List<Cake> cakes = new ArrayList<>();
		ExecutorService exec = Executors.newCachedThreadPool();
		int i = -1;
		while(cakes.size() <= 10) {
			if(i < waiters.size()-1 ) i++;
			else i = 0;
			Cake cake = new Cake(getName(i));
			exec.execute(new Packer(cake));
			exec.execute(new Labeler(cake));
			cakes.add(cake);
		}
		OrnamentalGarden.sleep(3000);
		for(Cake cake : cakes)
			System.out.println(cake);
		
		exec.shutdownNow();
		
	}
	
	
	@Test
	public void test() {
		Car car = new Car();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new WaxOff(car));
		exec.execute(new WaxOn(car));
		OrnamentalGarden.sleep(6000);
		exec.shutdownNow();
	}
	
}
