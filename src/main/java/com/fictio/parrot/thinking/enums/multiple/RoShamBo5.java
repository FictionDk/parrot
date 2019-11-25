package com.fictio.parrot.thinking.enums.multiple;

import java.util.EnumMap;
import static com.fictio.parrot.thinking.enums.multiple.Outcome.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum RoShamBo5 implements Competitor<RoShamBo5> {
	PAPER,SCISSORS,ROCK;
	
	static EnumMap<RoShamBo5, EnumMap<RoShamBo5,Outcome>>
		table = new EnumMap<>(RoShamBo5.class);
	
	// 使用静态代码块初始化 表结构
	static {
		initRow(PAPER, DRAW, LOSE, WIN);
		initRow(SCISSORS, WIN, DRAW, LOSE);
		initRow(ROCK, LOSE, WIN, DRAW);
	}
	
	static void initRow(RoShamBo5 it,Outcome vPaper,Outcome vScisssors,Outcome vRock) {
		EnumMap<RoShamBo5,Outcome> row = table.getOrDefault(it, new EnumMap<>(RoShamBo5.class));
		row.put(PAPER, vPaper);
		row.put(SCISSORS, vScisssors);
		row.put(ROCK, vRock);

		table.put(it, row); // 不能少
		//printTable();
	}
	
	static void printTable() {
		table.forEach((k,v)->{
			log.debug("{} -- {} ",k,v);
		});
	}
	
	@Override
	public Outcome compete(RoShamBo5 t) {
		return table.get(this).get(t);
	}
	
	public static void main(String[] args) {
		RoShamBo.play(RoShamBo5.class, 10);
	}

}
