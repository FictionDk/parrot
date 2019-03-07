package com.fictio.parrot.thinking.clazz;

import org.junit.Test;

/**
 * <p>Java class各个部分是按需加载
 *
 */
public class T02SweetShop {

	@Test
	public void test() {
		System.out.println("Start test");
		new Candy();
		System.out.println("After creating Candy");
		try {
			Class.forName("Gum");
		} catch (ClassNotFoundException e) {
			System.out.println("Could not find Gum");
		}
		new Cookie();
		System.out.println("After creating Cookie");
	}
	
}

class Candy {
	static { System.out.println("Loading Candy !"); }
}

class Gum {
	static { System.out.println("Loading Gum !");}
}

class Cookie {
	static { System.out.println("Loading Cookie !");}
}

