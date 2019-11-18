package com.fictio.parrot.thinking.enums;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Mail {
	enum GeneralDelivery { YES,NO1,NO2,NO3,NO4,NO5 }
	enum Scannability { UNSCANNABLE,YES1,YES2,YES3}
	enum Readability { ILLEGIBLE,YES1,YES2,YES3 }
	enum Address { INCORRECT, OK1, OK2, OK3 }
	enum ReturnAddress { MISSING,OK1, OK2, OK3 }
	enum ForwardDelivery { YES,NO1,NO2,NO3 }
	GeneralDelivery generalDelivery;
	Scannability scannability;
	Readability readability;
	Address address;
	ReturnAddress returnAddress;
	ForwardDelivery forwardDelivery;
	static long counter = 0;
	long id = counter++;
	public String toString() {
		return "Mail " + id;
	}
	public String details() {
		return toString() +
				", GeneralDelivery: " + generalDelivery +
				", Scannability: " + scannability +
				", Readability: " + readability +
				", Address: " + address +
				", ReturnAddress: " + returnAddress +
				", ForwardDelivery: " + forwardDelivery;
	}
	
	public static Mail randomMail() {
		Mail m = new Mail();
		m.generalDelivery = EnumUtils.random(GeneralDelivery.class);
		m.scannability = EnumUtils.random(Scannability.class);
		m.readability = EnumUtils.random(Readability.class);
		m.address = EnumUtils.random(Address.class);
		m.returnAddress = EnumUtils.random(ReturnAddress.class);
		m.forwardDelivery = EnumUtils.random(ForwardDelivery.class);
		return m;
	}
	
	public static Iterable<Mail> generator(final int count) {
		return new Iterable<Mail>() {
			int n = count;
			@Override
			public Iterator<Mail> iterator() {
				return new Iterator<Mail>() {
					@Override
					public Mail next() {
						return randomMail();
					}
					@Override
					public boolean hasNext() {
						return n-- > 0;
					}
				};
			}
		};
	}
	
}

@Slf4j
public class PostOffice {
	// 实现职责链,enum定义的次序决定了策略应用时的次序
	enum MailHandler {
		GENERAL_DELIVERY {
			boolean handle(Mail m) {
				switch (m.generalDelivery) {
				case YES:
					log.debug("Using general delivery for {}", m);
					return true;
				default: return false;
				}
			}
		},
		MACHINE_SCAN {
			@Override
			boolean handle(Mail m) {
				switch (m.scannability) {
				case UNSCANNABLE:
					return false;
				default:
					log.debug("Delivering {} automatically",m);
					return true;
				}
			}
		},
		VISUAL_INSPECTION {
			@Override
			boolean handle(Mail m) {
				switch (m.readability) {
				case ILLEGIBLE:
					return false;
				default:
					switch (m.address) {
					case INCORRECT:
						return false;
					default:
						log.debug("Delivering {} normally",m);
						return true;
					}
				}
			}
		},
		RETURN_TO_SENDER {
			@Override
			boolean handle(Mail m) {
				switch (m.returnAddress) {
				case MISSING:
					return false;
				default:
					log.debug("Returing {} to sender",m);
					return true;
				}
			}
		},
		FORWARD_DELIVERY {
			@Override
			boolean handle(Mail m) {
				switch (m.forwardDelivery) {
				case YES:
					log.debug("Forward Delivery for {}",m);
					return true;
				default: return false;
				}
			}
		};
		abstract boolean handle(Mail m);
	}
	
	static void handle(Mail m) {
		for(MailHandler handle : MailHandler.values()) {
			if(handle.handle(m)) return;
		}
		log.debug("{} is a dead letter!!",m);
	}
	
	interface Command {
		boolean action(Mail m);
	}
	
	EnumMap<MailHandler, Command> enumMaps = new EnumMap<>(MailHandler.class);
	@Before
	public void init() {
		for(MailHandler handler : MailHandler.values()) {
			enumMaps.put(handler, m->handler.handle(m));
		}
	}
	// 使用Enum实现"职责链"
	public void handleByMaps(Mail m) {
		for(Map.Entry<MailHandler, Command> mc : enumMaps.entrySet()) {
			if(mc.getValue().action(m)) return;
		}
		log.debug("{} is a dead letter!!",m);		
	}
	
	@Test
	public void mailPostTest() {
		for(Mail m : Mail.generator(20)) {
			log.debug("details => {}",m.details());
			//handle(m);
			handleByMaps(m);
			log.debug(" **** ");
		}
	}
	
}
