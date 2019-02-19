package com.fictio.parrot.thinking.thread;
import org.junit.Test;

// 使用接口,利于拓展
class SelfManaged implements Runnable {
	private int countDown = 5;
	private Thread t = new Thread(this);
	public SelfManaged() {
		t.start();
	}
	public String toString() {
		return Thread.currentThread().getName() + "(" + countDown + "),";
	}
	public void run() {
		while(true) {
			System.out.println(this);
			if(--countDown == 0) return;
		}
	}
}

// 使用继承,不利于拓展
public class SimpleThreadDemo extends Thread {
	private int countDown = 5;
	private static int threadCount = 0;
	public SimpleThreadDemo() {
		super("Thread-"+Integer.toString(++threadCount));
		start();
	}
	public String toString() {
		return "#" + getName() + "(" + countDown + "),";
	}
	public void run() {
		while(true) {
			System.out.println(this);
			// 尝试加Thread.yield() 或者不加;
			Thread.yield();
			if(-- countDown == 0) return;
		}
	}
	
	@Test
	public void simpleThreadTest() {
		for(int i = 0; i < 3; i++)
			new SimpleThreadDemo();
		new BaseThreadTest().confireWatingForEnd();
	}
	
	@Test
	public void selfManagedTest() {
		for(int i = 0; i < 4; i++) new SelfManaged();
		new BaseThreadTest().confireWatingForEnd();
	}
}
