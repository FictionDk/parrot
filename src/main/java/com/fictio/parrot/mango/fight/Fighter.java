package com.fictio.parrot.mango.fight;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("serial")
class MatcherQueue extends LinkedBlockingQueue<Fighter>{};

@Slf4j
public class Fighter implements Runnable {
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
	private LogQueue logQueue;
	private ReentrantLock attackLock;
	// 谨慎值
	private int cautiousValue;
	
	private int getAttack() {
		return this.attack+(this.rand.nextInt(10)-cautiousValue);
	}
	
	private int getDefend() {
		return this.defend+(this.rand.nextInt(10)-cautiousValue);
	}
	
	public void setMatchers(MatcherQueue mqueue) {
		this.matchers = mqueue;
	}
	
	public void setLogQueue(LogQueue lqueue) {
		this.logQueue = lqueue;
	}
	
	public void setLock(ReentrantLock lock) {
		this.attackLock = lock;
	}
	
	public Fighter (String name,int rand,int speed,int defend) {
		this.name = name;
		this.blood = 200;
		this.attack = 9;
		this.speed = speed;
		this.defend = defend;
		this.rand = new Random(rand);
		this.cautiousValue = rand;
		this.status = FighterStatus.SURVIVE;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("姓名:").append(this.name).append(";速度:");
		if(speed <= 1) sb.append(speed).append(",奇快"); 
		else if(speed > 1 && speed < 3 ) sb.append(speed).append(",一般");
		else sb.append(speed).append("很慢");
		sb.append(";性格:").append(cautiousValue);
		if(cautiousValue <= 2) sb.append("保守");
		else if(cautiousValue > 2 && cautiousValue < 4) sb.append("一般");
		else sb.append("狂放;");
		
		return sb.toString();
	}

	@Override
	public void run() {
		try {
			log.info("{} -- {}",this.name, (!Thread.interrupted() && FighterStatus.SURVIVE.equals(this.status)));
			while(!Thread.interrupted() && FighterStatus.SURVIVE.equals(this.status)) {
				if(this.matcher == null && this.matchers != null && this.matchers.size() > 0) 
					this.matcher = this.matchers.peek();
				attack();
				TimeUnit.MILLISECONDS.sleep(10*speed);
			}
		} catch (Exception e) {
			log.error("Thread Interrupted! {}",e.toString());
		}
	}
	
	private synchronized boolean needWait() {
		if(this.attackLock == null || this.logQueue == null || FighterStatus.DOWN.equals(this.status) || 
				this.matcher == null || FighterStatus.DOWN.equals(this.matcher.status)) return true;
		else return false;
	}

	/**
	 * <p>遭受攻击
	 * 
	 * @param actualAttack
	 * @return boolean 如果倒地,返回true,否则false
	 */
	private synchronized boolean getHurt(int actualAttack) {
		this.blood -= actualAttack;
		//log.info("{} -- {} -- {}",this.matcher.name,this.matcher.blood,actualAttack);
		if(this.blood <= 0 ) {
			this.status = FighterStatus.DOWN;
			this.logQueue.add(new EventLog(getFailedMsg(), this.matcher, this, 0));
			return true;
		}
		return false;
	}
	
	public void attack() throws InterruptedException {
		if(needWait()) {
			//log.info("{},wating {}|{}|{}|{}|{}|{}",this.name,this.attackLock!=null,this.logQueue!=null,
			//		this.status,this.matchers,this.matcher!=null,this.matcher != null?this.matcher.status:"null");
			TimeUnit.MILLISECONDS.sleep(500);
			Thread.yield();
			return;
		}
		this.attackLock.lock();
		
		try {
			int actualAttack = this.getAttack() - matcher.getDefend();
			this.logQueue.add(new EventLog(getAttackMsg(), this, this.matcher, actualAttack));
			this.logQueue.add(new EventLog(getDefMsg(actualAttack), this.matcher, this, actualAttack));
			if(actualAttack > 0) {
				if(this.matcher.getHurt(actualAttack)) {
					this.matcher = this.matchers.poll();
				}
			}
		}finally {
			this.attackLock.unlock();
		}
	}
	
	private String getAttackMsg() {
		StringBuilder sb = new StringBuilder(this.name)
							.append(" 挥拳打向 ").append(this.matcher.name);
		return sb.toString();
	}
	
	private String getDefMsg(int actualHeart) {
		StringBuilder sb = new StringBuilder(this.matcher.name);
		if(actualHeart <= 0) sb.append(" 躲过了 ").append(this.name).append(" 的攻击");
		else sb = sb.append(" 被击中, 受到[").append(actualHeart).append("]点伤害");
		return sb.toString();
	}
	
	private String getFailedMsg() {
		StringBuilder sb = new StringBuilder(this.matcher.name).append("体力不支,倒地不起");
		return sb.toString();
	}
}
