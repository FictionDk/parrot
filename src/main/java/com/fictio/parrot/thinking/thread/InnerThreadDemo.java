package com.fictio.parrot.thinking.thread;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

abstract class BaseInner {
	int countDown = 5;
}

/**
 * 使用内部类
 * @author dk
 *
 */
@Slf4j
class InnerThread1 extends BaseInner{
    protected Inner inner;
	private class Inner extends Thread {
		Inner(String name){
			super(name);
			start();
		}
		public void run() {
			while(true) {
				log.info("this: {}",this);
				if(--countDown == 0) return;
			}
		}
		public String toString() {
			return getName() + ": "+countDown;
		}
	}
	public InnerThread1(String name) {
		this.inner = new Inner(name);
	}
}

/**
 * 使用匿名内部类
 * 
 * @author dk
 *
 */
@Slf4j
class InnerThread2 extends BaseInner {
	private Thread t;
	public InnerThread2(String name) {
		t = new Thread(name) {
			public void run() {
				log.info("this: {}",this);
				if(-- countDown == 0) return;
			}
			public String toString() {
				return getName() + ": "+countDown;
			}
		};
		t.start();
	}
}

@Slf4j
class InnerRunable1 extends BaseInner {
	protected Inner inner;
	private class Inner implements Runnable {
		Thread t;
		public Inner(String name) {
			t = new Thread(this,name);
			t.start();
		}
		@Override
		public void run() {
			while(true) {
				log.info("this:{}",this);
				if(--countDown == 0) return;
			}
		}
		public String toString() {
			return t.getName()+": "+countDown;
		}
	}
	public InnerRunable1(String name) {
		inner = new Inner(name);
	}
}

@Slf4j
class InnerRunable2 extends BaseInner {
	private Thread t;
	public InnerRunable2(String name) {
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					log.info("this:{}",this);
					if(--countDown == 0) return;
				}
			}
			public String toString() {
				return Thread.currentThread().getName()+": "+countDown;
			}
		},name);
		t.start();
	}
}

@Slf4j
class ThreadMethod extends BaseInner {
	private Thread t;
	private String name;
	public ThreadMethod(String name) {
		this.name = name;
	}
	public void runTask() {
		if(t == null) {
			t = new Thread(name) {
				public void run() {
					while(true) {
						log.info("this: {}",this);
						if(--countDown == 0) return;
					}
				}
				public String toString() {
					return getName() + ": "+countDown;
				}
			};
		}
		t.start();
	}
}

public class InnerThreadDemo {
	
	@Test
	public void test() {
		new InnerThread1("InnerThread1");
		new InnerThread2("InnerThread2");
		new InnerRunable1("InnerRunable1");
		new InnerRunable2("InnerRunable2");
		new ThreadMethod("ThreadMethod");
		
		new BaseThreadTest().confireWatingForEnd();
	}
	
	
}
