package com.fictio.parrot.jvm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * VM args: -Xss128k
 * 
 * @author fictio
 *
 */
@Slf4j
public class JavaVMStackSOF {
	private int stackLength = 1;
	public void statckLeak() {
		stackLength++;
		statckLeak();
	}
	@Test
	public void test() {
		JavaVMStackSOF oom = new JavaVMStackSOF();
		try {
			oom.statckLeak();
		} catch (Throwable ex) {
			log.error("satck length: {}",stackLength);
			throw ex;
		}
	}
}
