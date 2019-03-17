package com.fictio.parrot.thinking.thread;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class WaxOMatic {

	private abstract class Person {
		String name;
		int blood;
		public Person(String name) {
			this.name = name;
			this.blood = 100;
		}
	}
	
    class Knight extends Person{
		int attackPower;
		boolean survive;
		boolean go = false;
		public Knight(String name) {
			super(name);
			survive = true;
			attackPower = 10 + new Random().nextInt(20);
		}
		public synchronized void attack() {
			log.info("Attack,{}",this);
			go = true;
			notifyAll();
		}
		public synchronized void defensive() {
			log.info("Defensive,{}",this);
			go = false;
			notifyAll();
		}
		public synchronized void waitForAttack() throws InterruptedException {
			while(go == false) {
				log.info("wait for attack,{}",this);
				wait();
			}
		}
		public synchronized void waitForDefensive() throws InterruptedException {
			while(go == true) {
				log.info("wait for defensive,{}",this);
				wait();
			}
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{attackPower:").append(attackPower).append(", survive:").append(survive).append(", go:")
					.append(go).append(", name:").append(name).append(", blood:").append(blood).append("}");
			return builder.toString();
		}
	}
	
	private class Attack implements Runnable {
		private Knight knightA;
		@SuppressWarnings("unused")
		private Knight knightB;
		public Attack(Knight knightA,Knight knightB) {
			this.knightA = knightA;
			this.knightB = knightB;
		}
		public void run() {
			while(!Thread.interrupted()) {
				knightA.attack();
				try {
					knightA.waitForDefensive();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Defensive implements Runnable {
		private Knight knightA;
		@SuppressWarnings("unused")
		private Knight knightB;
		public Defensive(Knight knightA,Knight knightB) {
			this.knightA = knightA;
			this.knightB = knightB;
		}
		public void run() {
			while(!Thread.interrupted()) {
				knightA.defensive();
				try {
					knightA.waitForAttack();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	List<String> waiters = Arrays.asList("Tom","Joe","Bin","Air","Alica","Ginduluman",
			"Talibon","Tabilaran","Alon","Anda","Tibigon","Jao");

	private int count = 0;
	
	private String getName(int id) {
		return waiters.get(id);
	}
	
	private int currentId() {
		if(count > waiters.size()-1) count = 0;
		else count++;
		return count;
	}
	
	@Test
	public void test2() {
		Knight k1 = new Knight(getName(currentId()));
		Knight k2 = new Knight(getName(currentId()));
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Attack(k1, k2));
		exec.execute(new Defensive(k1, k2));
		
		OrnamentalGarden.sleep(2000);
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
	
	
	private class TaskA implements Runnable {
		
		public synchronized void waitPrint() throws InterruptedException {
			log.info("Read for wait.");
			wait();
			log.info("Yes, I Got it.");
		}
		
		public synchronized void awake() {
			notifyAll();
		}
		
		public void run() {
			log.info("Goting in A");
			try {
				waitPrint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class TaskB implements Runnable {
		private TaskA taskA;
		private TaskB(TaskA task) {
			taskA = task;
		}
		public void run() {
			log.info("Going in B");
			OrnamentalGarden.sleep(1000);
			awake();
		}
		
		private synchronized void awake() {
			log.info("Awake, please!");
			taskA.awake();
		}
		
	}
	
	/**
	 * A进入任务,然后使用wait()挂起
	 * B获取A对象,进入任务,等待,使用A对象的唤醒
	 * 
	 */
	@Test
	public void test3() {
		ExecutorService exec = Executors.newCachedThreadPool();
		TaskA a = new TaskA();
		exec.execute(a);
		exec.execute(new TaskB(a));
		OrnamentalGarden.sleep(4000);
		exec.shutdownNow();
	}
	
	
}
