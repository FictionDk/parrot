package com.fictio.parrot.thinking.enums;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

enum Explore { HERE, THERE }

@Slf4j
public class Reflection {
	
	public Set<String> analyze(Class<?> enumClass){
		log.debug("Staring ....");
		log.debug("Base Class: {}",enumClass.getSuperclass());
		for(Type t : enumClass.getGenericInterfaces()) {
			log.debug("Type: {}",t);
		}
		
		Set<String> methods = new TreeSet<>();
		for(Method m : enumClass.getMethods()) {
			methods.add(m.getName());
			// 确定方法values()是static
			if(m.getName().equals("values")) log.debug("{}",m);
			// 确定方法valueOf()在编译阶段被重写
			if(m.getName().equals("valueOf")) log.debug("valueOf {}",m.getGenericReturnType());
		}
		log.debug("Counts: {}",methods.size());
		log.debug("Methods: {}",methods);
		log.debug("Ending ....");
		return methods;
	}
	
	@Test
	public void test() {
		Set<String> exloreMethods = analyze(Explore.class);
		Set<String> enumMethods = analyze(Enum.class);
		
		exloreMethods.removeAll(enumMethods);
		
		/**
		 * 1. values方法是由编译器添加的static方法
		 * 2. 创建Expore的过程中,编译器为其添加了valueOf()方法
		 *        原有Enum中的valueOf(...)两个参数
		 *        新增Expore中的valueOf(...)一个参数
		 * 3. 编译器将Expore标记为final类
		 */
		log.debug("Left Metods : {}",exloreMethods);
		
		/**
		 * 4. Class中有getEnumConstants()方法获取所有枚举值,但如果不是枚举类需注意空指针
		 */
		log.debug("Vals : {}",Arrays.toString(Explore.class.getEnumConstants()));
	}
	
	
}
