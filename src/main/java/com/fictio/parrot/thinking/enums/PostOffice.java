package com.fictio.parrot.thinking.enums;

import java.util.Iterator;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Mail {
	enum GeneralDelivery { YES,NO1,NO2,NO3,NO4,NO5 }
	enum Scannability { UNSCANNABLE,YES1,YES2,YES3,YES4 }
	enum Readability { ILLEGIBLE,YES1,YES2,YES3,YES4 }
	enum Address { INCORRECT, OK1, OK3, OK4, OK5, OK6 }
	enum ReturnAddress { MISSING,ok1, ok2, ok3, ok4, ok5 }
	GeneralDelivery generalDelivery;
	Scannability scannability;
	Readability readability;
	Address address;
	ReturnAddress returnAddress;
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
				", ReturnAddress: " + returnAddress;
	}
	
	public static Mail randomMail() {
		Mail m = new Mail();
		m.generalDelivery = EnumUtils.random(GeneralDelivery.class);
		m.scannability = EnumUtils.random(Scannability.class);
		m.readability = EnumUtils.random(Readability.class);
		m.address = EnumUtils.random(Address.class);
		m.returnAddress = EnumUtils.random(ReturnAddress.class);
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
		};
		abstract boolean handle(Mail m);
	}
	
	static void handle(Mail m) {
		for(MailHandler handle : MailHandler.values()) {
			if(handle.handle(m)) return;
		}
	}
	
	@Test
	public void mailPostTest() {
		for(Mail m : Mail.generator(10)) {
			log.debug("details => {}",m.details());
			handle(m);
			log.debug(" **** ");
		}
	}
}
