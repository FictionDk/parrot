package com.fictio.parrot.thinking.enums;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import static com.fictio.parrot.thinking.enums.AlarmPoints.*;

interface Command {
	void action();
}

@Slf4j
public class EnumMaps {

	@Test
	public void test() {
		// 命令设计模式
		EnumMap<AlarmPoints, Command> em = new EnumMap<>(AlarmPoints.class);
		em.put(KITCHEN, ()->{log.debug("kitchen fired!");});
		em.put(BATHROOM, ()->{log.debug("bathroom alert!");});
		
		for(Map.Entry<AlarmPoints, Command> e : em.entrySet()) {
			e.getValue().action();
		}

		// Java8 Lambda
		Command c = em.getOrDefault(OFFICE1, ()->{log.debug("warning, no choose.");});
		c.action();
	}
}
