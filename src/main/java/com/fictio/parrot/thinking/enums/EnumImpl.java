package com.fictio.parrot.thinking.enums;

import java.util.Random;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

interface Generator<T> {
	T next();
}

/**
 * enum类都继承 @see{java.lang.Enum},enum不能再继承,但可以实现接口
 */
@Slf4j
public class EnumImpl {
	enum CartoonCharacter implements Generator<CartoonCharacter> {
		SLAPPY,SPANKY,PUNCHY,SILLY,BOUNCY,NUTTY,BOB;
		private Random rand = new Random(47);
		public CartoonCharacter next() {
			return values()[rand.nextInt(values().length)];
		}
	}
	public <T> void printNext(Generator<T> rg) {
		log.debug("{}",rg.next());
	}
	
	enum Character {
		SLAPPY,SPANKY,PUNCHY,SILLY,BOUNCY,NUTTY,BOB;
		private static Random rand = new Random(47);
		public static Character next() {
			return values()[rand.nextInt(values().length)];
		}
	}
	
	@Test
	public void test() {
		CartoonCharacter cc = CartoonCharacter.BOB;
		// 使用接口方式实现
		for(int i = 0; i < 10; i++) printNext(cc);
		log.debug("++++++++++++++++++++++++++++++++++++++++");
		// 使用静态方法实现
		for(int i = 0; i < 10; i++) log.debug("{}",Character.next());
		
	}

}
