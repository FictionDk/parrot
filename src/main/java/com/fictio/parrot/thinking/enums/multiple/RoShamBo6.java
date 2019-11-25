package com.fictio.parrot.thinking.enums.multiple;

import static com.fictio.parrot.thinking.enums.multiple.Outcome.*;

public enum RoShamBo6 implements Competitor<RoShamBo6> {
	PAPER,SCISSORS,ROCK;
	
	private static Outcome[][] table = {
			{DRAW,LOSE,WIN}, // paper
			{WIN,DRAW,LOSE}, // scissors
			{LOSE,WIN,DRAW}  // rock
	};

	@Override
	public Outcome compete(RoShamBo6 t) {
		return table[this.ordinal()][t.ordinal()];
	}
	
	public static void main(String[] args) {
		RoShamBo.play(RoShamBo6.class, 10);
	}

}
