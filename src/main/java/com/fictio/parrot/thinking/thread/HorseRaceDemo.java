package com.fictio.parrot.thinking.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Horse implements Runnable {
	private static int counter = 0 ;
	private final int id = counter++;
	private int strides = 0;
	private static CyclicBarrier barrier;
	private static Random rand = new Random(55);
	public Horse(CyclicBarrier b) {
		barrier = b;
	}
	public synchronized int getStrides() {
		return strides;
	}
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					strides += rand.nextInt(3);
				}
				barrier.await();
			}
		} catch (InterruptedException e) {
			log.error("A legitimate way to exit, {}",e.toString());
		} catch (BrokenBarrierException e) {
			log.error("Error, {}",e.toString());
			new RuntimeException(e);
		}
	}
	// 每匹马每回合移动一次
	public String tracks() {
		StringBuffer s = new StringBuffer();
		for(int i = 0; i < getStrides(); i++) {
			s.append("*");
		}
		s.append(id);
		return s.toString();
	}
	@Override
	public String toString() {
		return "Horse_"+id;
	}
}

@Slf4j
class HorseRace {
	
	static final int FINISH_LINE = 45;
	
	private List<Horse> horses = new ArrayList<>();
	
	private ExecutorService exec = Executors.newCachedThreadPool();
	
	private CyclicBarrier barrier;
	
	public HorseRace(int nHorses, final int pause) {
		
		barrier = new CyclicBarrier(nHorses, ()-> {
			StringBuffer s = new StringBuffer();
			for(int i = 0; i < FINISH_LINE; i++) s.append("=");
			// 栅栏打印
			log.info("XXXXX: {}",s);
			for(Horse h : horses) log.info("Horse: {}",h.tracks());
			for(Horse h : horses) {
				if(h.getStrides() >= FINISH_LINE) {
					log.info("{} Won !",h);
					exec.shutdownNow();
					return;
				}
			}
			
			OrnamentalGarden.sleep(pause);
		});
		
		
		for(int i = 0; i < nHorses; i++) {
			Horse h = new Horse(barrier);
			horses.add(h);
			exec.execute(h);
		}
	}
}

/**
 * CyclicBarrier 循环的屏障
 * 
 * @author dk
 *
 */
public class HorseRaceDemo {

	@Test
	public void test() {
		int nHorses = 7;
		int pause = 200;
		new HorseRace(nHorses, pause);
		OrnamentalGarden.sleep(9000);
	}
}
