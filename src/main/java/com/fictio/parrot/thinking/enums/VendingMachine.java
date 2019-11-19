package com.fictio.parrot.thinking.enums;

import java.util.EnumMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import static com.fictio.parrot.thinking.enums.Input.*;

enum Input {
	NICKEL(5),DIME(10),QUARTER(25),DOLLAR(100),
	TOOTHPASTE(200),CHIPS(75),SODA(100),SOAP(50),
	ABORT_TRANSACTION {
		public int amount() {
			throw new RuntimeException("Abort.amount()");
		}
	},STOP {
		public int amount() {
			throw new RuntimeException("Shut_down.amount()");
		}
	};
	private int val;
	private Input(int val) {
		this.val = val;
	}
	private Input() {}
	public int amount() {
		return this.val;
	}
	public static Random rand = new Random();
	public static Input randomSelection() {
		return values()[rand.nextInt(values().length-1)];
	}
}


enum Category {
	MONEY(NICKEL,DIME,QUARTER,DOLLAR),
	ITEM_SELECTION(TOOTHPASTE,CHIPS,SODA,SOAP),
	QUIT_TRANSACTION(ABORT_TRANSACTION),
	SHUT_DOWN(STOP);
	
	private Input[] values;
	private Category(Input... types) {
		this.values = types;
	}
	private static EnumMap<Input, Category> categories = new EnumMap<>(Input.class);
	static {
		for(Category c : Category.class.getEnumConstants())
			for(Input type : c.values)
				categories.put(type, c);
	}
	public static Category categorize(Input input) {
		return categories.getOrDefault(input, null);
	}
}

@Slf4j
public class VendingMachine {
	
	public void categoryTest() {
		log.debug("{}",Category.categorize(NICKEL));
		log.debug("{}",Category.categorize(SOAP));
		log.debug("{}",Category.categorize(ABORT_TRANSACTION));
	}
	
	private static int amount = 0;
	private static State state = State.RESTING;
	private static Input selection = null;
	enum StateDuration { TRANSIENT }
	
	enum State {
		RESTING {
			void next(Input input) {
				log.debug("{} -> {}","Resting",input);
				switch (Category.categorize(input)) {
				case MONEY:
					amount += input.amount();
					state = ADDING_MONEY;
					break;
				case SHUT_DOWN:
					state = TERMINAL;
				default:
					break;
				}
			}
		},
		ADDING_MONEY {
			void next(Input input) {
				log.debug("{} -> {}","Adding Money",input);
				switch (Category.categorize(input)) {
				case MONEY:
					amount += input.amount();
					break;
				case ITEM_SELECTION:
					selection = input;
					if(amount < selection.amount())
						log.debug("Insufficient money for {}",selection);
					else 
						state = DISPENSING;
					break;
				case QUIT_TRANSACTION:
					state = GIVING_CHANGE;
					break;
				case SHUT_DOWN:
					state = TERMINAL;
				default:
					break;
				}
			}
		},
		DISPENSING(StateDuration.TRANSIENT) {
			void next() {
				log.debug("here is your {}",selection);
				amount -= selection.amount();
				state = GIVING_CHANGE;
			}
		},
		GIVING_CHANGE(StateDuration.TRANSIENT) {
			void next() {
				if(amount > 0) {
					log.debug("your change is {}",amount);
					amount = 0;
				}
				state = RESTING;
			}
		},
		TERMINAL {
			void outPut() {
				log.debug("Halted");
			}
		};
		
		private boolean isTransient = false;
		private State() {}
		private State(StateDuration trans) {
			this.isTransient = true;
		}
		void next() {
			throw new RuntimeException("Only call next() for StateDuration.TRANSIENT states!");
		}
		void next(Input input) {
			throw new RuntimeException("Only call next() for none-transient states!");
		}
		void outPut() {
			log.debug("{}",amount);
		}
	}

	void run(Generator<Input> gen) {
		while(state != State.TERMINAL) {
			state.next(gen.next());
			while(state.isTransient) state.next();
			state.outPut();
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class RandomInputGenerator implements Generator<Input>{
		@Override
		public Input next() {
			Input input = Input.randomSelection();
			log.debug("==> {}",input);
			return input;
		}
	}
	
	@Test
	public void mainTest() {
		// categoryTest();
		Generator<Input> gen = new RandomInputGenerator();
		run(gen);
	}
}
