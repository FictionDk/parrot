package com.fictio.parrot.thinking.enums;

import java.util.Random;

public class EnumUtils {
	private static Random rand = new Random(47);
	// T<extends Enum<T>> 表示 T 是 Enum 的一个实例
	public static <T extends Enum<T>> T random(Class<T> ec) {
		return random(ec.getEnumConstants());
	}
	public static <T> T random(T[] values) {
		return values[rand.nextInt(values.length)];
	}
}
