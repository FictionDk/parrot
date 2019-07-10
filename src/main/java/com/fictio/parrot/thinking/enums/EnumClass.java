package com.fictio.parrot.thinking.enums;


import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

//import static com.fictio.parrot.thinking.enums.Shrubbery.*;
import static com.fictio.parrot.thinking.enums.Spiciness.*;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

/**
 * Basic enums introduce
 * Enums static import
 * 
 * @author fictio
 *
 */
@Slf4j
public class EnumClass {

	enum Shrubbery{
		GROUND,CRAWLING,HANGING
	}
	
	@Data
	class Burrito{
		Spiciness degree;
		public Burrito(Spiciness degree) {
			this.degree = degree;
		}
	}
	
	@Test
	public void basic() {
		for(String s : "GROUND,CRAWLING,HANGING".split(",")) {
			Shrubbery shrub = Enum.valueOf(Shrubbery.class, s);
			log.info("{}",shrub);
		}
		
		for(Shrubbery s : Shrubbery.values()) {
			log.info("{} compareTo {} is {}",s,Shrubbery.CRAWLING,s.compareTo(Shrubbery.CRAWLING));
			log.info("{} equals {} is {}",s,Shrubbery.CRAWLING,s.equals(Shrubbery.CRAWLING));
			log.info("{} == {} is {}",s,Shrubbery.CRAWLING,s == Shrubbery.CRAWLING);
			log.info("declaringclass={},name={},ordinal={}",s.getDeclaringClass(),
					s.name(),s.ordinal());
			log.info("======================================================");
		}
	}
	
	/**
	 * Static import, can make the enums use more easy;
	 */
	@Test
	public void staticImport() {
		log.info("{}",new Burrito(FLAMING));
		log.info("{}",new Burrito(MEDIUM));
	}

	/**
	 * Export enums by reflection
	 * Method for values is static method add by ---
	 * 
	 */
	@Test
	public void reflection() {
		Set<String> spicinessMethods = getMethods(Spiciness.class);
		Set<String> enumsMethods = getMethods(Enum.class);
		
		for(String s : spicinessMethods) {
			log.info("{}",s);
		}
		log.info("================================");
		for(String s : enumsMethods) {
			log.info("{}",s);
		}
		log.info("================================");
		spicinessMethods.removeAll(enumsMethods);
		
		for(String s : enumsMethods) {
			log.info("{}",s);
		}
		
		
	}

	private Set<String> getMethods(Class<?> clazz){
		Set<String> methods = new TreeSet<>();
		for(Method m : clazz.getMethods()) methods.add(m.getName());
		return methods;
	}
	
}
