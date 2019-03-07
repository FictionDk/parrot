package com.fictio.parrot.thinking.clazz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class Individual {
	String name;
}

class Pet extends Individual {
}

class Dog extends Pet {
}

class Mutt extends Dog {
}

class Pug extends Dog {
}

class Cat extends Pet {
}

class Manx extends Cat {
}

abstract class PetCreator {
	private Random rand = new Random();
	public abstract List<Class<? extends Pet>> types();
	public Pet randomPet() {
		int n = rand.nextInt(types().size());
		try {
			return types().get(n).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		} 
	}
	public Pet[] createArray(int size) {
		Pet[] result = new Pet[size];
		for(int i = 0; i < size; i++)
			result[i] = randomPet();
		return result;
	}
	public List<Pet> arrayList(int size){
		List<Pet> result = new ArrayList<>(size);
		Collections.addAll(result, createArray(size));
		return result;
	}
}

final class ForNameCreator extends PetCreator {
	private static List<Class<? extends Pet>> types = new ArrayList<>();
	private static String[] typeNames = {"Pug","Mutt","Manx"};
	private static String packageName = "com.fictio.parrot.thinking.clazz.";
	
	@SuppressWarnings("unchecked")
	private static void loader() {
		try {
			for(String name : typeNames)
				types.add((Class<? extends Pet>) Class.forName(packageName+name));
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
	}
	
	static { loader(); }
	
	@Override
	public List<Class<? extends Pet>> types() {
		return types;
	}
}

final class LiteralPetCreator extends PetCreator {
	public static final List<Class<? extends Pet>> allTypes = 
			Collections.unmodifiableList(Arrays.asList(
					Pet.class,Cat.class,Dog.class,Mutt.class,Pug.class,Mutt.class,Manx.class));
	
	private static final List<Class<? extends Pet>> types = 
			allTypes.subList(allTypes.indexOf(Mutt.class), allTypes.size());

	@Override
	public List<Class<? extends Pet>> types() {
		return types;
	}
	
}


@Slf4j
public class T03Individual {
	static class PetCounter extends HashMap<String, Integer> {
		private static final long serialVersionUID = 6153873726372344534L;
		public void count(String type) {
			Integer quantity = get(type);
			if(quantity == null) put(type,1);
			else put(type,quantity+1);
		}
	}
	
	public static void countPets(PetCreator creator) {
		PetCounter counter = new PetCounter();
		for(Pet pet : creator.createArray(20)) {
			if(pet instanceof Pet) counter.count("Pet");
			if(pet instanceof Dog) counter.count("Dog");
			if(pet instanceof Cat) counter.count("Cat");
			if(pet instanceof Mutt) counter.count("Mutt");
			if(pet instanceof Pug) counter.count("Pug");
			if(pet instanceof Manx) counter.count("Manx");
		}
		log.info("counter : {}",counter);
	}
	
	@Test
	public void test() {
		countPets(new ForNameCreator());
	}
}
