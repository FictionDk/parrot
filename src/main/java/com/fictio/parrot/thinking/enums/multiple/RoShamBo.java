package com.fictio.parrot.thinking.enums.multiple;

import lombok.extern.slf4j.Slf4j;

import com.fictio.parrot.thinking.enums.EnumUtils;

@Slf4j
public class RoShamBo {
	
	public static <T extends Competitor<T>> void match(T a,T b) {
		log.debug("{} vs. {} : {}",a,b,a.compete(b));
	}
	
	public static <T extends Enum<T> & Competitor<T>> void play(Class<T> rsbClass, int size) {
		for(int i = 0; i < size; i++)
			match(EnumUtils.random(rsbClass),EnumUtils.random(rsbClass));
	}

}
