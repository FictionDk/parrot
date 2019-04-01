package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Fighter implements Runnable{
	private enum FighterStatus {
		SURVIVE,DEAD;
	}
	private String name;
	private FighterStatus status;
	private int attack;
	// private int defend;
	private Random rand;
	// private double speed;
	private int blood;
	private Fighter matcher;
	private CyclicBarrier barrier;
	
	public Fighter (Fighter matcher,String name,int rand) {
		this.name = name;
		this.matcher = matcher;
		this.blood = 100;
		this.attack = 9;
		this.rand = new Random(rand);
		this.status = FighterStatus.SURVIVE;
	}
	
	public void setMatcher(Fighter matcher) {
		this.matcher = matcher;
	}
	
	public void setBarrier(CyclicBarrier barrier) {
		this.barrier = barrier;
	}
	
	public void run() {
		try {
			while(!isEnd()) {
				attack();
				barrier.await();
			}
		} catch (Exception e) {
			log.error("Thread Interrupted! {}",e.toString());
		}
	}
	
	public synchronized boolean isEnd() {
		if(Thread.interrupted() || FighterStatus.DEAD.equals(this.status)
				|| FighterStatus.DEAD.equals(this.matcher.status)) return true;
		else return false;
	}
	
	public synchronized void attack() {
		log.info("{} --> {} starting ...",this,matcher);
		int actualAttack = rand.nextInt(10)+attack;
		this.matcher.blood = this.matcher.blood - actualAttack;
		if(this.matcher.blood <= 0 ) {
			this.matcher.status = FighterStatus.DEAD;
		}
		log.info("{} --> {} ending ...",this,matcher);
	}
	
	public String toString() {
		return "Fighter_"+name+";Blood_"+this.blood;
	}
	
}

@Slf4j
public class FightRoud {
	
	private static int counter = 0;
	
	@Test
	public void test() {
		Fighter a = new Fighter(null, "铁牛",10);
		Fighter b = new Fighter(a, "提辖",20);
		a.setMatcher(b);
		
		CyclicBarrier barrier = new CyclicBarrier(2, ()->{
			log.info("======={}=========",counter++);
		});
		
		a.setBarrier(barrier);
		b.setBarrier(barrier);
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(b);
		exec.execute(a);
		
		OrnamentalGarden.sleep(6000);
		exec.shutdownNow();
	}
	

}
