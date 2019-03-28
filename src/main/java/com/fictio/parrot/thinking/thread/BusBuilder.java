package com.fictio.parrot.thinking.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Bus {
	private final int id;
	private boolean
		engine = false,drivenTrain = false,wheels = false;
	public Bus(int id) {this.id = id;}
	public Bus() {this.id = -1;}
	public synchronized int getId() {
		return this.id;
	}
	public synchronized void addEngine() {
		this.engine = true;
	}
	public synchronized void addDrivenTrain() {
		this.drivenTrain = true;
	}
	public synchronized void addWheels() {
		this.wheels = true;
	}
	public synchronized String toString() {
		return "Bus_"+id+"["+"engine:"+engine+
				";drivenTrain"+drivenTrain+";wheels:"+wheels+"]";
	}
}

@SuppressWarnings("serial")
class BusQueue extends LinkedBlockingQueue<Bus>{}

@Slf4j
class ChassisBuilder implements Runnable {
	private BusQueue busQueue;
	private int counter = 0;
	public ChassisBuilder(BusQueue queue) {
		this.busQueue = queue;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				TimeUnit.MILLISECONDS.sleep(400);
				Bus b = new Bus(counter++);
				log.info("ClassBuilder created {}",b);
				busQueue.put(b);
			}
		} catch (Exception e) {
			log.error("Thread interrupted: ChassisBuilder, {}",e.toString());
		}
		log.info("ChassisBuilder off");
	}
}

class RobotPool {
	private Set<Robot> pool = new HashSet<>();
	public synchronized void add(Robot r) {
		pool.add(r);
		notifyAll();
	}
	public synchronized void hire(Class<? extends Robot> robotType,Assembler d) 
			throws InterruptedException {
		for(Robot r : pool) {
			if(r.getClass().equals(robotType)) {
				pool.remove(r);
				r.assignAssember(d);
				r.engage();
				return;
			}
		}
		wait();
		hire(robotType, d);
	}
	public synchronized void release(Robot r) {
		add(r);
	}
}

@Slf4j
abstract class Robot implements Runnable {
	private RobotPool pool;
	public Robot(RobotPool p) {
		this.pool = p;
	}
	protected Assembler assembler;
	public Robot assignAssember(Assembler assember) {
		this.assembler = assember;
		return this;
	}
	// 工作
	private boolean engage = false;
	public synchronized void engage() {
		engage = true;
		notifyAll();
	}
	abstract protected void performService();
	@Override
	public void run() {
		try {
			powerDown();
			while(!Thread.interrupted()) {
				performService();
				assembler.berrier().await();
				powerDown();
			}
		} catch (InterruptedException e) {
			log.error("Exiting {} interruption",this);
		} catch (BrokenBarrierException e) {
			throw new RuntimeException(e);
		}
		log.info("{} -- OFF",this);
	}
	
	private synchronized void powerDown() throws InterruptedException {
		engage = false;
		assembler = null;
		pool.release(this);
		while(engage == false) wait();
	}
	
	public String toString() {
		return getClass().getName();
	}
}

@Slf4j
class EngineRobot extends Robot {
	public EngineRobot(RobotPool p) {
		super(p);
	}
	@Override
	protected void performService() {
		log.info("{} installing engine",this);
		assembler.bus().addEngine();
	}
}

@Slf4j
class DrivenTrainRobot extends Robot {
	public DrivenTrainRobot(RobotPool p) {
		super(p);
	}
	@Override
	protected void performService() {
		log.info("{} installing drivenTrain",this);
		assembler.bus().addDrivenTrain();
	}
}

@Slf4j
class WheelRobot extends Robot {
	public WheelRobot(RobotPool p) {
		super(p);
	}
	@Override
	protected void performService() {
		log.info("{} installing wheelrobot",this);
		assembler.bus().addWheels();
	}
}


// 装配者
@Slf4j
class Assembler implements Runnable {
	private BusQueue chassisQueue,finishingQueue;
	private Bus bus;
	private CyclicBarrier barrier = new CyclicBarrier(4);
	private RobotPool robotPool;
	public Assembler(BusQueue cq,BusQueue fq,RobotPool rp) {
		this.chassisQueue = cq;
		this.finishingQueue = fq;
		this.robotPool = rp;
	}
	public Bus bus() {return bus;}
	public CyclicBarrier berrier() {return barrier;}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				bus = chassisQueue.take();
				robotPool.hire(EngineRobot.class, this);
				robotPool.hire(DrivenTrainRobot.class, this);
				robotPool.hire(WheelRobot.class, this);
				berrier().await();
				finishingQueue.put(bus);
			}
		} catch (InterruptedException e) {
			log.info("Exiting Assembler via interrupt");
		} catch (BrokenBarrierException e) {
			throw new RuntimeException(e);
		}
		log.info("Assembler OFF");
	}
	
}

/**
 * <p> 多线程汽车装配仿真
 * 
 * @author dk
 *
 */
@Slf4j
public class BusBuilder {
	
	/**
	 * ChassisBuilder -> OriBus -> chassisQueue:
	 *   Assembler: chassisQueue -> OriBus
	 *     - OriBus-> EngineRobot
	 *     - OriBus-> DrivenTrainRobot
	 *     - OriBus-> WheelRobot
	 *   -> finishBus -> finishingQueue
	 */
	@Test
	public void test() {
		BusQueue chassisQueue = new BusQueue(),
				 finishingQueue = new BusQueue();
		RobotPool p = new RobotPool();
		
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new EngineRobot(p));
		exec.execute(new DrivenTrainRobot(p));
		exec.execute(new WheelRobot(p));
		exec.execute(new Assembler(chassisQueue, finishingQueue, p));
		
		exec.execute(()->{
			try {
				while(!Thread.interrupted()) log.info("Report: {}",finishingQueue.take());
			} catch (InterruptedException e) {
				log.info("Exiting Report via interruption");
			}
			log.info("Report OFF");
		});
		
		exec.execute(new ChassisBuilder(chassisQueue));
		
		OrnamentalGarden.sleep(6000);
		exec.shutdownNow();
	}

}
