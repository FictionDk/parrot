package com.fictio.parrot.jvm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * VM args: -Xms10m -Xmx10m -XX:+HeapDumpOnOutOfMemoryError
 * -XX:+PrintGCDetails
 * 
 * @author fictio
 *
 */
public class HeapOOM {
	static class OOMObject{
	}
	@Test
	public void test(){
		List<OOMObject> list = new ArrayList<>();
		while(true) list.add(new OOMObject());
	}
}
