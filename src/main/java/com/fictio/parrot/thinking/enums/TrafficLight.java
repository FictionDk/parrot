package com.fictio.parrot.thinking.enums;

import lombok.extern.slf4j.Slf4j;

enum Signal {
	GREEN,YELLOW,RED
}

@Slf4j
public class TrafficLight {
	
	Signal color = Signal.RED;
	public void change() {
		switch (color) {
		case RED:
			color = Signal.GREEN;
			break;
		case GREEN:
			color = Signal.YELLOW;
			break;
		case YELLOW:
			color = Signal.RED;
			break;
		}
	}
	public String toString() {
		return "The traffic light is :" + color;
	}
	public static void main(String[] args) {
		TrafficLight t = new TrafficLight();
		for(int i = 0; i < 9; i++) {
			log.debug("{}",t);
			t.change();
		}
	}
}
