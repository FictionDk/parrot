package com.fictio.parrot.algorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * <p> 贪心算法
 * 
 * @author fictio
 *
 */
@Slf4j
public class Sort01Greedy {
	
	private boolean sleep = false;
	
	private int len = 20;
	
	public void openDebug() {
		this.sleep = true;
		this.len = 6;
	}

	/**
	 * <p>原版贪心算法排序,关注当下,即当前两个比对的元素
	 * 
	 * @param ints
	 * @param n
	 */
	public void naiveGnomesort(Integer[] ints, int n) {
		for(int i = 1; i < n;) {
			if(i < 1 || ints[i-1] <= ints[i]) {
				i++;
			}else{
				AlgorithmUtils.swap(ints,i-1,i);
				i--;
			}
			goSleep(ints, i);
		}
	}
	
	public void improvedGnomesort(Integer[] ints, int n) {
		for(int k = 1; k < n; k ++) 
			for(int i = k; 0<i && ints[i-1] > ints[i]; i--) {
				AlgorithmUtils.swap(ints, i-1, i);goSleep(ints, i);
			}
	}

	private void goSleep(Integer[] ints, int i) {
		if(sleep)
			try {
				log.info("i={},ints={}",i,Arrays.toString(ints));
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@Test
	public void sortTest() {
		boolean isDebug = true;
		if(isDebug) openDebug();
		
		Integer[] ints = getRandomInts();
		//naiveGnomesort(ints, ints.length);
		improvedGnomesort(ints, ints.length);
		log.info("Ends: {}",Arrays.toString(ints));
	}

	private Integer[] getRandomInts() {
		Random rand = new Random();
		int len = this.len;
		Integer[] ints = new Integer[len];
		for(int i = 0; i < len; i++) {
			ints[i] = rand.nextInt(100);
		}
		log.info("Origin:{}",Arrays.toString(ints));
		return ints;
	}
	
}
