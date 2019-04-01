package com.fictio.parrot.thinking.thread;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
	private int defend;
	private Random rand;
	private int speed;
	private int blood;
	private Fighter matcher;
	
	private ReentrantLock attackLock;

	private RoundQueue queue;
	
	@SuppressWarnings("unused")
	private CyclicBarrier barrier;
	
	public Fighter (String name,int rand,int speed,int defend) {
		this.name = name;
		this.blood = 200;
		this.attack = 9;
		this.speed = speed;
		this.defend = defend;
		this.rand = new Random(rand);
		this.status = FighterStatus.SURVIVE;
	}
	
	public synchronized double getSpeed() {
		return this.rand.nextInt(10)+speed;
	}
	
	public synchronized int getDefend() {
		return this.defend+this.rand.nextInt(10);
	}
	
	public synchronized int getAttack() {
		return this.attack+this.rand.nextInt(10);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setMatcher(Fighter matcher) {
		this.matcher = matcher;
	}
	
	public void setBarrier(CyclicBarrier barrier) {
		this.barrier = barrier;
	}
	
	public void setLock(ReentrantLock lock) {
		this.attackLock = lock;
	}
	
	public void setQueue(RoundQueue queue) {
		this.queue = queue;
	}
	
	public void run() {
		try {
			while(!isEnd()) {
				attack();
				TimeUnit.MILLISECONDS.sleep(10*speed);
				//barrier.await();
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
	
	public void attack() {
		if(isEnd()) return;
		
		this.attackLock.lock();
		EventLog eventLog = null;
		try {
			log.info("{} 挥拳打向 {} ",this.name,matcher.name);
			int actualAttack = this.getAttack() - matcher.getDefend();
			eventLog = new EventLog(this.name+" 捶 "+matcher.name, this, this.matcher, actualAttack);
			if(actualAttack > 0) {
				this.matcher.blood = this.matcher.blood - actualAttack;
				log.info("{} 被击中, 受到 [{}] 点伤害" ,matcher.name,actualAttack);
			}else {
				log.info("{} 躲过了 {} 的攻击",this.matcher.name,this.name);
			}
			if(this.matcher.blood <= 0 ) {
				this.matcher.status = FighterStatus.DEAD;
				log.info("{} 被击倒在地,爬不起来 ",matcher.name);
			}
			this.queue.add(eventLog);
		}finally {
			this.attackLock.unlock();
		}
	}
	
	public String toString() {
		return "Fighter_"+name+";Blood_"+this.blood;
	}
	
}

@SuppressWarnings("serial")
class RoundQueue extends LinkedBlockingQueue<EventLog>{};

class EventLog {
	private static int counter = 0;
	private final int id = counter++;
	private Fighter a;
	private Fighter d;
	private int attack;
	private String msg;
	public EventLog(String msg,Fighter a,Fighter d,int attack) {
		this.a = a;
		this.d = d;
		this.attack = attack;
		this.msg = msg;
	}
	public Fighter getAttacker() {
		return a;
	}
	public Fighter getDefender() {
		return d;
	}
	public int getAttackValue() {
		return attack;
	}
	public String toString() {
		return this.id+"_"+this.msg+";heart: "+attack;
	}
}

@Slf4j
public class FightRoud {
	
	private RoundQueue queue = new RoundQueue();
	
	@Test
	public void test() {
		ReentrantLock lock = new ReentrantLock();
		// 姓名,随机种子,速度(越高越慢),防御(越高约强)
		Fighter a = new Fighter("铁牛",10,1,7);
		Fighter b = new Fighter("提辖",20,5,10);
		a.setMatcher(b);
		b.setMatcher(a);
		a.setLock(lock);
		b.setLock(lock);
		a.setQueue(queue);
		b.setQueue(queue);

		CyclicBarrier barrier = new CyclicBarrier(2, ()->{
		});

		a.setBarrier(barrier);
		b.setBarrier(barrier);
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(b);
		exec.execute(a);
		
		OrnamentalGarden.sleep(10000);
		exec.shutdownNow();
		
		log.info("=================================================================");
		String msg = resultStatic(a, b);
		log.info("MSG: {}",msg);
	}
	
	
	private String resultStatic(Fighter a,Fighter b) {
		int aAttackCount = 0,bAttackCount = 0;
		int aAttackValue = 0,bAttackValue = 0;
		while(!queue.isEmpty()) {
			EventLog e = queue.poll();
			log.info("{}",e);
			if(e.getAttacker().getName().equals(a.getName())) {
				aAttackCount++;
				if(e.getAttackValue() > 0)
					aAttackValue += e.getAttackValue();
			}else {
				bAttackCount++;
				if(e.getAttackValue() > 0)
					bAttackValue += e.getAttackValue();
			}
		}
		
		String msg = "{name}出手 {count} 次,累计输出伤害 {value}";
		
		String msgA = msg.replace("{name}", a.getName())
						 .replace("{count}", ""+aAttackCount)
						 .replace("{value}", ""+aAttackValue);
				
		String msgB = msg.replace("{name}", b.getName())
				 .replace("{count}", ""+bAttackCount)
				 .replace("{value}", ""+bAttackValue);		
		
		return msgA+";"+msgB;
	}

}
