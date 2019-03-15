package com.fictio.parrot.thinking.thread;

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
