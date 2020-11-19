package com.fictio.parrot.algorithm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Greedy.java
 * @Description: 线性遍历
 */
@Slf4j
public class LcLinear {

    private int[] numbers_a = {7,1,5,3,6,4,5};
    private int[] numbers_b = {1,2,3,4,5};
    private int[] numbers_c = {7,6,4,3,1};
    private int[] numbers_d = {6,1,3,2,4,7,7};

    @Test
    public void test() {
        log.debug(">>prices={}, maxProfit={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>prices={}, maxProfit={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>prices={}, maxProfit={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>prices={}, maxProfit={}",numbers_d,containsDuplicate(numbers_d));
    }

    public boolean containsDuplicate(int[] nums) {
        for(int i = 0; i < nums.length; i++) {
            for(int j = i + 1; j < nums.length; j++) {
                if(nums[i] == nums[j]) return true;
            }
        }
        return false;
    }
}
