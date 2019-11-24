package com.fictio.parrot.thinking.enums.multiple;

import static com.fictio.parrot.thinking.enums.multiple.Outcome.*;

import java.util.Random;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

interface Item {
	Outcome compete(Item it);
	Outcome eval(Rock rock);
	Outcome eval(Scissors scissors);
	Outcome eval(Paper paper);
	//default method cannot override Object class;
	//default String toString() {
	//	return Item.class.getSimpleName();
	//}
}

class Paper implements Item {
	@Override
	public Outcome compete(Item it) {
		return it.eval(this);
	}
	public Outcome eval(Rock rock) {
		return LOSE;
	}
	public Outcome eval(Scissors scissors) {
		return WIN;
	}
	public Outcome eval(Paper paper) {
		return DRAW;
	}
	public String toString() {
		return "Paper";
	}
}

class Rock implements Item {
	@Override
	public Outcome compete(Item it) {
		return it.eval(this);
	}
	@Override
	public Outcome eval(Rock rock) {
		return DRAW;
	}
	@Override
	public Outcome eval(Scissors scissors) {
		return LOSE;
	}
	@Override
	public Outcome eval(Paper paper) {
		return WIN;
	}
	public String toString() {
		return "Rock";
	}
}

class Scissors implements Item {
	@Override
	public Outcome compete(Item it) {
		return it.eval(this);
	}
	@Override
	public Outcome eval(Rock rock) {
		return WIN;
	}
	@Override
	public Outcome eval(Scissors scissors) {
		return DRAW;
	}
	@Override
	public Outcome eval(Paper paper) {
		return LOSE;
	}
	public String toString() {
		return "Scissors";
	}
}

@Slf4j
public class RoShamBo1 {
	private static class ItemFactory {
		private static Random random = new Random(47);
		public static Item buildItem() {
			return buildItem(random.nextInt(3));
		}
		public static Item buildItem(int i) {
			Item item = null;
			switch (i) {
			case 0:
				item = new Paper(); 
				break;
			case 1:
				item = new Rock();
				break;
			case 2:
				item = new Scissors();
				break;
			default:
				throw new RuntimeException("Err index for build Item,"+i);
			}
			return item;
		}
	}
	
	public void match(Item a, Item b) {
		log.debug("{} vs. {} : {}",a,b,a.compete(b));
	}
	
	@Test
	public void mainTest() {
		for(int i = 0; i < 20; i++) 
			match(ItemFactory.buildItem(), ItemFactory.buildItem());
	}
}
