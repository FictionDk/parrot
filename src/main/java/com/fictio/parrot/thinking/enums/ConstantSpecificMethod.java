package com.fictio.parrot.thinking.enums;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * enum常量相关方法
 *
 */
@Slf4j
public class ConstantSpecificMethod {

	public enum Constant {
		DATE_TIME {
			String getInfo() {
				return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}
		},
		CLASSPATH {
			String getInfo() {
				return System.getenv("CLASSPATH");
			}
		},
		VERSION {
			String getInfo() {
				return System.getProperty("java.version");
			}
		};
		abstract String getInfo();
	}
	
	@Test
	public void enumConstantTest() {
		for(Constant c : Constant.values()) {
			log.debug("{}.getInfo() => {}",c,c.getInfo());
		}
	}

	public enum Cycle {
		UNDERBODY {
			void action() { log.debug("Spraying the underbody"); }
		},
		WHEELWASH {
			void action() { log.debug("Washing the wheels"); }
		},
		PREWASH {
			void action() { log.debug("Lossening the dirt"); }
		},
		BASIC {
			void action() { log.debug("The basic wash"); }
		},
		HOTWAX {
			void action() { log.debug("Applying hot wax"); }
		},
		RINSE {
			void action() { log.debug("Rinsing"); }
		},
		BLOWDRY {
			void action() { log.debug("Blowing dry"); }
		};
		abstract void action();
	}
	
	class CarWash {
		EnumSet<Cycle> cycles = EnumSet.of(Cycle.BASIC,Cycle.RINSE);
		public void add(Cycle c) {
			cycles.add(c);
		}
		public void washCar() {
			for(Cycle c : cycles) c.action();
		}
		@Override
		public String toString() {
			return cycles.toString();
		}
	}
	
	@Test
	public void washerTest() {
		CarWash wash = new CarWash();
		log.debug("washer ==> {}",wash);
		wash.washCar();
		wash.add(Cycle.BLOWDRY);
		wash.add(Cycle.HOTWAX);
		wash.add(Cycle.HOTWAX);
		log.debug("washer ==> {}",wash);
		wash.washCar();
	}
	
}
