package com.fictio.parrot.mango.fight;


import java.util.concurrent.Executors;

import org.junit.Test;

import com.fictio.parrot.thinking.thread.OrnamentalGarden;

public class FightTest {
	
	@Test
	public void test() {
		//OrnamentalGarden.sleep(16000);
		
		Round r = new Round();
		// 姓名,随机值(1~6),speed(1-5),防守(6,10)
		r.addFighter(new Fighter("野狗", 1, 4, 3), "guiwang");
		r.addFighter(new Fighter("书书", 2, 2, 7), "qingyun");
		
		OrnamentalGarden.sleep(100);
		
		r.addFighter(new Fighter("青龙", 3, 1, 5), "guiwang");
		
		OrnamentalGarden.sleep(3000);
		
		r.addFighter(new Fighter("雪琪", 2, 1, 4), "qinyun");
		
		Executors.newCachedThreadPool().execute(()->{
			LogQueue logs = r.getLogs();
			while(!Thread.interrupted()) {
				if(!logs.isEmpty()) System.out.println(logs.poll());
			}
		});
		
		OrnamentalGarden.sleep(20000);
		
	}

}
