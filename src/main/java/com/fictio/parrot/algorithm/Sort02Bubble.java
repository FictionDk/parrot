package com.fictio.parrot.algorithm;

import java.util.Arrays;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>冒泡排序
 * @author fictio
 *
 */
@Slf4j
public class Sort02Bubble {
	
	/**
	 * <p> 原始版本冒泡排序
	 * 
	 * @param ints
	 */
	public void naiveBubbleSort(Integer[] ints) {
		for(int i = 0; i < ints.length ; i++) {
			boolean sorted = true;
			for(int j = 0; j < ints.length-i-1; j++) {
				if(ints[j] < ints[j+1]) AlgorithmUtils.swap(ints, j, j+1);
				log.info("j={},arrs={}",j,Arrays.toString(ints));
				sorted = false;
				AlgorithmUtils.sleep(1);
			}
			// 确保当无交换进行时,不需要排序
			if(sorted) break;
		}
	}
	
	/**
	 * <p> 简单升级版本的冒泡排序,记录交换位置;
	 * 
	 * @param ints
	 */
	public void improvedBubbleSort(Integer[] ints) {
		int i = ints.length -1;
		while(i > 0) {
			int pos = 0;
			for(int j=0;j<i;j++) {
				if(ints[j+1] < ints[j]) {
					AlgorithmUtils.swap(ints, j, j+1);
					pos = j;
				}
				log.info("j={},arrs={}",j,Arrays.toString(ints));
				AlgorithmUtils.sleep(1);
			}
			i = pos;
		}
	}

	@Test
	public void test() {
		Integer[] ints = AlgorithmUtils.buildRandArray(6);
		//naiveBubbleSort(ints);
		improvedBubbleSort(ints);
	}
}
