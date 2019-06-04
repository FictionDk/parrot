package com.fictio.parrot.mango.fight;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Round {
	
	private MatcherQueue guiwangZong;
	private MatcherQueue qingyunMen;
	private LogQueue logs;
	private ReentrantLock lock = new ReentrantLock();
	ExecutorService exec = Executors.newCachedThreadPool();
	
	public Round() {
		this.guiwangZong = new MatcherQueue();
		this.qingyunMen = new MatcherQueue();
		this.logs = new LogQueue();
	}
	
	public void addFighter(Fighter fighter,String sectName) {
		fighter.setLock(lock);
		fighter.setLogQueue(logs);
		
		if("guiwang".equals(sectName)) {
			guiwangZong.add(fighter);
			fighter.setMatchers(qingyunMen);
		} else {
			qingyunMen.add(fighter);
			fighter.setMatchers(guiwangZong);
		}
		
		logs.add(new EventLog(loginMsg(fighter,sectName), fighter, null, 0));
		
		exec.execute(fighter);
	}
	
	public LogQueue getLogs() {
		return this.logs;
	}
	
	private String loginMsg(Fighter f,String sectName) {
		return new StringBuilder(f.toString()).append(" 加入 [").append(sectName).append("]").toString();
	}

}
