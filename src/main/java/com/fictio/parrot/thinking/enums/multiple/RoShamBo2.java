package com.fictio.parrot.thinking.enums.multiple;

import static com.fictio.parrot.thinking.enums.multiple.Outcome.*;

interface Competitor<T> {
	Outcome compete(T t);
}

public enum RoShamBo2 implements Competitor<RoShamBo2> {
	// 二维数组的雏形
	PAPER(DRAW,LOSE,WIN),
	SCISSORS(WIN,DRAW,LOSE),
	ROCK(LOSE,WIN,DRAW)
	;
	private Outcome vPaper,vScissors,vRock;
	RoShamBo2(Outcome vPaper,Outcome vScissors,Outcome vRock){
		this.vPaper = vPaper;
		this.vScissors = vScissors;
		this.vRock = vRock;
	}
	public Outcome compete(RoShamBo2 it) {
		switch (it) {
		default:
		case PAPER: return vPaper;
		case SCISSORS: return vScissors;
		case ROCK: return vRock;
		}
	}
	
	public static void main(String[] args) {
		RoShamBo.play(RoShamBo2.class, 15);
	}
}
