package com.fictio.parrot.thinking.enums.multiple;

import static com.fictio.parrot.thinking.enums.multiple.Outcome.*;

public enum RoShamBo4 implements Competitor<RoShamBo4> {
	ROCK {
		public Outcome compete(RoShamBo4 opponent) {
			return compete(SCISSORS, opponent);
		}
	},
	PAPER {
		public Outcome compete(RoShamBo4 opponent) {
			return compete(ROCK, opponent);
		}
	},
	SCISSORS {
		public Outcome compete(RoShamBo4 opponent) {
			return compete(PAPER, opponent);
		}
	};

	// 将自己的loser作为参数传到自己的方法中进行比对
	// 如果自己有多个loser(比较链条加长),复杂度会成倍增长,从而变得不适用
	public Outcome compete(RoShamBo4 loser,RoShamBo4 opponent) {
		return ((opponent == this) ? DRAW : ((opponent == loser) ? WIN : LOSE));
	}
	
	@Override
	public Outcome compete(RoShamBo4 t) {
		return this.compete(t);
	}
	
	public static void main(String[] args) {
		RoShamBo.play(RoShamBo4.class, 15);
	}

}
