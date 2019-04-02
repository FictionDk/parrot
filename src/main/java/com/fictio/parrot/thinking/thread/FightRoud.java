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

@SuppressWarnings("serial")
class MatcherQueue extends LinkedBlockingQueue<Fighter>{};

@Slf4j
class Fighter implements Runnable{
	
	private enum FighterStatus {
		SURVIVE,DOWN;
	}
	private String name;
	private FighterStatus status;
	private int attack;
	private int defend;
	private Random rand;
	private int speed;
	private int blood;
	private Fighter matcher;
	private MatcherQueue matchers;
	
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
	
	public void setMatchers(MatcherQueue matchers) {
		this.matchers = matchers;
	}
	
	public void addMatcher(Fighter matcher) {
		if(this.matchers == null) this.matchers = new MatcherQueue();
		this.matchers.add(matcher);
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
	
	/**
	 * <p>遭受攻击
	 * 
	 * @param actualAttack
	 * @return boolean 如果倒地,返回true,否则false
	 */
	private boolean getHurt(int actualAttack) {
		this.blood -= actualAttack;
		if(this.blood <= 0 ) {
			this.status = FighterStatus.DOWN;
			log.info("{} 被击倒在地,爬不起来 ",getName());
			return true;
		}
		return false;
	}
	
	public void run() {
		try {
			if(this.matcher == null) this.matcher = this.matchers.take();
			while(!isEnd()) {
				attack();
				TimeUnit.MILLISECONDS.sleep(10*speed);
				//barrier.await();
			}
		} catch (Exception e) {
			log.error("Thread Interrupted! {}",e.toString());
			e.printStackTrace();
		}
	}
	
	public synchronized boolean isEnd() {
		if(Thread.interrupted() || FighterStatus.DOWN.equals(this.status) || this.matcher == null
				|| FighterStatus.DOWN.equals(this.matcher.status)) return true;
		else return false;
	}
	
	public void attack() {
		EventLog eventLog = null;
		if(isEnd()) {return;}
		
		this.attackLock.lock();
		try {
			log.info("{} 挥拳打向 {} ",this.name,matcher.getName());
			int actualAttack = this.getAttack() - matcher.getDefend();
			eventLog = new EventLog(this.name+" 捶 "+matcher.name, this, this.matcher, actualAttack);
			if(actualAttack > 0) {
				log.info("{} 被击中, 受到 [{}] 点伤害" ,matcher.name,actualAttack);
				if(this.matcher.getHurt(actualAttack)) {
					this.matcher = this.matchers.poll();
				}
					
			}else {
				log.info("{} 躲过了 {} 的攻击",this.matcher.name,this.name);
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
		Fighter a = new Fighter("铁牛",10,1,10);
		Fighter b = new Fighter("提辖",20,5,10);
		Fighter c = new Fighter("王二",30,5,7);
		a.addMatcher(b);
		a.addMatcher(c);
		b.addMatcher(a);
		c.addMatcher(a);
		a.setLock(lock);
		b.setLock(lock);
		c.setLock(lock);
		a.setQueue(queue);
		b.setQueue(queue);
		c.setQueue(queue);

/*		CyclicBarrier barrier = new CyclicBarrier(2, ()->{
		});
		a.setBarrier(barrier);
		b.setBarrier(barrier);*/
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(b);
		exec.execute(a);
		exec.execute(c);
		
		OrnamentalGarden.sleep(10000);
		exec.shutdownNow();
		
		log.info("=================================================================");
		String msg = resultStatic(a, b, c);
		log.info("MSG: {}",msg);
	}
	
	
	private String resultStatic(Fighter a,Fighter b,Fighter c) {
		int aAttackCount = 0,bAttackCount = 0,cAttackCount = 0;
		int aAttackValue = 0,bAttackValue = 0,cAttackValue = 0;
		while(!queue.isEmpty()) {
			EventLog e = queue.poll();
			log.info("{}",e);
			if(e.getAttacker().getName().equals(a.getName())) {
				aAttackCount++;
				if(e.getAttackValue() > 0)
					aAttackValue += e.getAttackValue();
			}else if(e.getAttacker().getName().equals(b.getName())) {
				bAttackCount++;
				if(e.getAttackValue() > 0)
					bAttackValue += e.getAttackValue();
			}else {
				cAttackCount++;
				if(e.getAttackValue()  > 0)
					cAttackValue += e.getAttackValue();
			}
		}
		
		String msg = "{name}出手 {count} 次,累计输出伤害 {value}";
		
		String msgA = msg.replace("{name}", a.getName())
				 .replace("{count}", ""+aAttackCount)
				 .replace("{value}", ""+aAttackValue);
		String msgB = msg.replace("{name}", b.getName())
				 .replace("{count}", ""+bAttackCount)
				 .replace("{value}", ""+bAttackValue);	

		String msgC = msg.replace("{name}", c.getName())
				 .replace("{count}", ""+cAttackCount)
				 .replace("{value}", ""+cAttackValue);		
		
		return msgA+";"+msgB+";"+msgC;
	}

}
