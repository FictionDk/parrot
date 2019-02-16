package com.fictio.parrot.demo;

import org.junit.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Parcel {
	class Contents {
		private int i = 11;
		public int value() {return i;}
	}
	class Destination {
		private String label;
		public Destination(String whereTo) {
			label = whereTo;
		}
		String readLabel() { return label;}
	}
	public Destination to(String s) {
		return new Destination(s);
	}
	public Contents contents() {
		return new Contents();
	}
	public void ship(String dest) {
		Contents c =  contents();
		Destination d = to(dest);
		log.info("d: {},c :{}",d.readLabel(),c.value());
	}
}

/**
 * 创建内部类练习
 */
class Outer {
	private String outerValue;
	public Outer () {
		this.outerValue = "Outer";
	}
	class Inner {
		private int value;
		Inner(int v) {
			this.value = v;
		}
		@Override
		public String toString() {
			return String.valueOf(value)+"|"+outerValue;
		}
	}
	public Inner getInner() {
		return new Inner(10);
	}
}

// 类似容器的迭代器
interface Selector {
	boolean end();
	Object current();
	void next();
}

class SeqItem {
	private int value;
	public SeqItem(int v) {
		this.value = v;
	}
	public String toString() {
		return String.valueOf(value);
	}
}

class Sequence {
	private Object[] items;
	private int next = 0;
	public Sequence (int size) {
		items = new Object[size];
	}
	public void add(Object x) {
		if(next < items.length) items[next++] = x;
		else throw new RuntimeException("Array size is letter");
	}
	// 对于容器Sequence的Selector(迭代器)的实现
	// 匿名内部类不需要任何条件就能访问外围类的所有元素访问权
	private class SequenceSelect implements Selector {
		public Sequence getSequence() {
			return Sequence.this;
		}
		private int i = 0;
		@Override
		public boolean end() {
			return i==getSequence().items.length;
		}
		@Override
		public Object current() {
			return items[i];
		}
		@Override
		public void next() {
			if(i < items.length) i++;
		}
	}
	public Selector selector() {
		return new SequenceSelect();
	}
}

/**
 * 使用.this 与 new
 *
 */
class DotThis {

	void f() {
		System.out.println("DotThis.f()");
	}
	
	public class Inner {
		public DotThis outer() {
			return DotThis.this;
		}
	}
	
	public Inner inner() {
		return new Inner();
	}
	
}



@Slf4j
public class InnerClassDemo01 {
	
	/**
	 * 创建内部类测试
	 */
	@Test
	public void parcelTest() {
		Parcel p = new Parcel();
		p.ship("Tasmania");
		Parcel q = new Parcel();
		// Defining references to inner classes
		Parcel.Contents c = q.contents();
		Parcel.Destination d = q.to("Borneo");
		log.info("c:{},d:{}",c.value(),d.readLabel());
	}
	
	@Test
	public void outerTest() {
		Outer.Inner inner = new Outer().getInner();
		log.info("Outer.inner : {}",inner);
	}

	/**
	 * 内部类链接到外部类
	 */
	@Test
	public void selectorTest() {
		Sequence seq = new Sequence(10);
		for(int i = 0; i < 10; i++) seq.add(new SeqItem(i));
		// (非静态)内部类需要外部类的实际对象,不能直接由类创建
		Selector selector = seq.selector();
		while(!selector.end()) {
			log.info("selector.current() = {}",selector.current());
			selector.next();
		}
	}
	
	/**
	 * this与new Test
	 */
	@Test
	public void dotThisTest() {
		DotThis dt = new DotThis();
		DotThis.Inner dti = dt.inner();
		// 使用this返回对象dt
		dti.outer().f();
		dt.f();
		// 使用new创建内部类Inner
		DotThis.Inner dtn = dt.new Inner();
		dtn.outer().f();
	}
	
}

/*OutPut:

*///:~




