package com.fictio.parrot.thinking.clazz;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class SuperClazz {
	public String toString() {
		return "SuperClazz";
	}
}

final class CountedInteger extends SuperClazz {
	private static long counter;
	// 每次新建对象就在原有的counter中累加
	private final long id = counter ++;
	public String toString() {
		return "CInt-"+Long.toString(id);
	}
}

/**
 * <p> 泛型引用类对象
 *
 */
@Slf4j
public class T02GenericClassRef {
	
	private class FilledList<T> {
		private Class<T> type;
		public FilledList(Class<T> type) {
			this.type = type;
		}
		/**
		 * <p>按需创建指定数量的type类型对象
		 * 
		 * @param nElements
		 * @return
		 */
		public List<T> create(int nElements){
			List<T> result = new ArrayList<>();
			try {
				for(int i = 0; i < nElements; i++) 
					result.add(type.newInstance());
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
			return result;
		}
	}
	
	@Test
	public void filledTest() {
		FilledList<CountedInteger> fl = new FilledList<>(CountedInteger.class);
		log.info("re: {}",fl.create(15));
	}
	
	@Test
	public void supperClazzTest() {
		Class<CountedInteger> cls = CountedInteger.class;
		//虽然CountedInteger的超类是SuperClazz,但下面的却无法编译
		//cls.getSuperClass()返回的只是表示某个类的超类
		//这种含糊性,造成其获取的只是Object类对象(实际是SuperClazz类对象)
		//Class<SuperClazz> supperCls = cls.getSuperclass();
		//获取超类
		Class<? super CountedInteger> supCls = cls.getSuperclass();
		try {
			Object obj = supCls.newInstance();
			log.info("SupObj : {}",obj);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

}
