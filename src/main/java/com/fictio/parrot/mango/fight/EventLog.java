package com.fictio.parrot.mango.fight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("serial")
class LogQueue extends LinkedBlockingQueue<EventLog>{};

public class EventLog {
	private static int counter = 0;
	private final int id = counter++;
	private Fighter a;
	private Fighter d;
	private int attack;
	private String msg;
	private LocalDateTime createTime;
	
	public EventLog(String msg,Fighter a,Fighter d,int attack) {
		this.a = a;
		this.d = d;
		this.attack = attack;
		this.msg = msg;
		this.createTime = LocalDateTime.now();
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
	public String getTime() {
		return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	public String toString() {
		return this.id+"_"+this.msg+";heart: "+attack;
	}
}
