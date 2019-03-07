package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Pair {
	private int x,y;
	public Pair () {}
	public Pair (int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() { return x; }
	public int getY() { return y; }
	public void incrementX() { x++; }
	public void incrementY() { y++; }
	public String toString() {
		return "{'x':"+x+",'y:'"+y+"}";
	}
	public class PairValuesNotEqualException extends RuntimeException {
		private static final long serialVersionUID = 7573901198744227303L;
		public PairValuesNotEqualException() {
			super("Pair values not equal: " + Pair.this);
		}
	}
	public void checkState() {
		if(x != y) throw new PairValuesNotEqualException();
	}
}

abstract class PairManager {
	AtomicInteger checkCount = new AtomicInteger(0);
	protected Pair p = new Pair();
	private List<Pair> storage = Collections.synchronizedList(new ArrayList<Pair>());
	public synchronized Pair getPair() {
		return new Pair(p.getX(),p.getY());
	}
	protected void store(Pair p) {
		storage.add(p);
		try {
			TimeUnit.MILLISECONDS.sleep(50);
		} catch (InterruptedException ignore) {}
	}
	public abstract void increment();
}

class PairManage1 extends PairManager {
	public synchronized void increment() {
		p.incrementX();
		p.incrementY();
		store(p);
	}
}

class PairManage2 extends PairManager {
	public synchronized void increment() {
		Pair temp;
		synchronized (this) {
			p.incrementX();
			p.incrementY();
			temp = getPair();
		}
		store(temp);
	}
}

class PairManipulator implements Runnable {
	private PairManager pm;
	public PairManipulator(PairManager pm) {
		this.pm = pm;
	}
	public void run() {
		while(true) pm.increment();
	}
	public String toString() {
		return "Pair: "+pm.getPair() + ";checkCount = "+pm.checkCount.get();
	}
}

class PairChecker implements Runnable {
	private PairManager pm;
	public PairChecker(PairManager pm) {
		this.pm = pm;
	}
	public void run() {
		while(true) {
			pm.checkCount.incrementAndGet();
			pm.getPair().checkState();
		}
	}
}

/**
 * <p>临界区: 同步代码块,阻止方法内部分代码被多线程访问;
 *
 */
@Slf4j
public class CriticalSection {
	
	public void testApproaches(PairManager pman1, PairManager pman2) {
		ExecutorService exec = Executors.newCachedThreadPool();
		PairManipulator pm1 = new PairManipulator(pman1);
		PairManipulator pm2 = new PairManipulator(pman2);
		
		PairChecker pchecker1 = new PairChecker(pman1);
		PairChecker pchecker2 = new PairChecker(pman2);
		
		exec.execute(pm1);
		exec.execute(pm2);
		exec.execute(pchecker1);
		exec.execute(pchecker2);
		
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			log.info("Sleep interrupted");
		}
		
		log.info("pm1: {}, pm2: {}, ",pm1, pm2);
		System.exit(0);
	}
	
	@Test
	public void test() {
		PairManager pman1 = new PairManage1();
		PairManager pman2 = new PairManage2();
		
		testApproaches(pman1, pman2);
		
		new ResouresThreadDemo().mainThreadSleep(10);
		
	}

}
