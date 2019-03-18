package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Toast {
	public enum Status {
		DRY, BUTTERED, PEANUT, JAMMED
	}
	private Status status = Status.DRY;
	private final int id;
	public Toast(int id) {this.id = id;}
	public void butter() {
		status = Status.BUTTERED;
	}
	public void peaNut() {
		status = Status.PEANUT;
	}
	public void jam() {
		status = Status.JAMMED;
	}
	public Status getStatus() { return this.status;}
	public int getId() { return this.id;}
	public String toString() {
		return "Toast: "+id+": "+status;
	}
}

@SuppressWarnings("serial")
class ToastQueue extends LinkedBlockingQueue<Toast> {}

@Slf4j
class Toaster implements Runnable {
	private ToastQueue queue;
	private int count = 0;
	private Random rand = new Random(50);
	public Toaster(ToastQueue queue) {
		this.queue = queue;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				TimeUnit.MILLISECONDS.sleep(100+rand.nextInt(500));
				Toast t = new Toast(count++);
				log.info("T: {}",t);
				/**
				 * Inserts the specified element at the tail of this queue, waiting if
				 * necessary for space to become available.
				 */
				queue.put(t);
				/**
				 * Inserts the specified element into this queue if it is possible to do so
				 * immediately without violating capacity restrictions, returning
				 * <tt>true</tt> upon success and throwing an <tt>IllegalStateException</tt>
				 * if no space is currently available.
				 *
				 * <p>This implementation returns <tt>true</tt> if <tt>offer</tt> succeeds,
				 * else throws an <tt>IllegalStateException</tt>.
				 */
				//queue.add(t);
			}
		} catch(Exception e) {
			log.error("Toaster interrupted ,{}",e.toString());
		}
		log.info("Toaster off");
	}
}

@Slf4j
class Butter implements Runnable {
	private ToastQueue dryQueue, butterQueue;
	public Butter(ToastQueue dry,ToastQueue buttered) {
		dryQueue = dry;
		butterQueue = buttered;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Toast t = dryQueue.take();
				t.butter();
				log.info("T: {}",t);
				butterQueue.put(t);
			}
		} catch (Exception e) {
			log.error("butter interrupted ,{}",e.toString());
		}
		log.info("butter off");
	}
}

@Slf4j
class PeaNut implements Runnable {
	private ToastQueue dryQueue,peaNutQueue;
	public PeaNut(ToastQueue dryQueue,ToastQueue peaNutQueue) {
		this.dryQueue = dryQueue;
		this.peaNutQueue = peaNutQueue;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Toast t = dryQueue.take();
				t.peaNut();
				log.info("T: {}",t);
				peaNutQueue.add(t);
			}
		} catch (Exception e) {
			log.error("peaNut interrupted");
		}
		log.info("peaNut off");
	}
}

@Slf4j
class Jammer implements Runnable {
	private ToastQueue butterQueue, peaNutQueue, finishQueue;
	public Jammer(ToastQueue buttered, ToastQueue peaNuted, ToastQueue finished) {
		this.butterQueue = buttered;
		this.finishQueue = finished;
		this.peaNutQueue = peaNuted;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Toast t = butterQueue.take();
				t.jam();
				log.info("T: {}",t);
				finishQueue.put(t);
				t = peaNutQueue.take();
				t.jam();
				log.info("T: {}",t);
				finishQueue.put(t);
			}
		} catch (Exception e) {
			log.error("jamer interrupted ,{}",e.toString());
		}
		log.info("Jamer off");
	}
}

/**
 * <p> 结果校验类
 *
 */
@Slf4j
class Eater implements Runnable {
	private ToastQueue finishQueue;
	private int counter = 0;
	public Eater(ToastQueue finished) {
		this.finishQueue = finished;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Toast t = finishQueue.take();
				if(t.getId() != counter++ || t.getStatus()!=Toast.Status.JAMMED) {
					log.info("check failed, >>> {}",t);
					System.exit(1);
				}else {
					log.info("chomp! {}",t);
					// 声明切换线程,可以让他多吃点
					Thread.yield();
				}
			}
		} catch (Exception e) {
			log.error("Eater interrupted");
		}
		log.info("Eater off");
	}
}


/**
 * <p>使用BlockingQueue,示例线程协作
 * 
 * @author dk
 *
 */
public class ToastOMatic {
	
	@Test
	public void test() {
		ToastQueue dryQueue = new ToastQueue(),
				butterQueue = new ToastQueue(),
				finishQueue = new ToastQueue(),
				peaNutQueue = new ToastQueue();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Toaster(dryQueue));
		exec.execute(new Butter(dryQueue, butterQueue));
		exec.execute(new PeaNut(dryQueue, peaNutQueue));
		exec.execute(new Jammer(butterQueue,peaNutQueue,finishQueue));
		
		// OrnamentalGarden.sleep(2000);
		exec.execute(new Eater(finishQueue));
		
		OrnamentalGarden.sleep(4000);
		exec.shutdownNow();
		
	}

}
