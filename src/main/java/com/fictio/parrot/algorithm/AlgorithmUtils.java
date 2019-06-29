package com.fictio.parrot.algorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmUtils {

	public static Integer[][] buildRandMatrix(Integer len){
		Random rand = new Random();
		if(len == null) len = 2 + rand.nextInt(4);
		Integer[][] matrix = new Integer[len][len];
		
		for(int i = 0; i < len; i++) {
			for(int j = i; j < len; j++) {
				matrix[i][j] = rand.nextInt(len+20);
			}
		}
		
		return matrix;
	}
	
	public static void swap(Integer[]ints, int i, int j) {
		int tmp = ints[i];
		ints[i] = ints[j];
		ints[j] = tmp;
	}
	
	public static Integer[] buildRandArray(Integer len) {
		Random rand = new Random();
		if(len == null) len = 8 + rand.nextInt(20);
		Integer[] ints = new Integer[len];
		for(int i = 0; i < len; i++) {
			ints[i] = rand.nextInt(100);
		}
		return ints;
	}
	
	public static void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void swapTest() {
		Integer i = 1; Integer j = 2;
		Integer[] ints = {12,17,11};
		AlgorithmUtils.swap(ints, i, j);
		log.info("{}",Arrays.toString(ints));
	}
	
	@Test
	public void matrixTest() {
		Integer[][] m = buildRandMatrix(3);
		for(Integer[] a : m) {
			log.info("{}",Arrays.toString(a));
		}
	}
	
}
