package com.fictio.parrot.mango.fight;


import org.junit.Test;

import com.fictio.parrot.thinking.thread.OrnamentalGarden;

public class FightTest {
	
	@Test
	public void test() {
		Round r = new Round();
		// 姓名,随机值(1~6),speed(1-5),防守(6,10)
		r.addFighter(new Fighter("野狗", 4, 3, 7), "guiwang");
		r.addFighter(new Fighter("书书", 3, 2, 9), "qingyun");
		
		OrnamentalGarden.sleep(100);
		
		r.addFighter(new Fighter("青龙", 3, 1, 9), "guiwang");
		
		OrnamentalGarden.sleep(200);
		
		LogQueue logs = r.getLogs();
		while(!logs.isEmpty())
			System.out.println(logs.poll());
		
		OrnamentalGarden.sleep(20000);
		
		while(!logs.isEmpty())
			System.out.println(logs.poll());
	}

}
