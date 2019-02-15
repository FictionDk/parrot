package com.fictio.parrot.demo;

import java.util.Arrays;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeetCode01 {
    
    @Test
    public void twoSumTest() {
        //int [] nums = {2,3,4,5};
        int [] nums = {3,3};
        int target = 6;
        int [] rs = twoSum(nums, target);
        log.info("rs : {}",Arrays.toString(rs)); 
    }
    
    public int[] twoSum(int[] nums, int target) {
        int [] result = new int[2];
        int begin = 0;
        int end = 0;
        for(int i = 0; i < nums.length; i++) {
            for(int j = i+1; j < nums.length; j++) {
                log.info("nums[{}]={},nums[{}]={},re={}",i,nums[i],j,nums[j],nums[i]+nums[j]);
                if((nums[i]+nums[j]) == target) {
                    begin = i;
                    end = j;
                    break;
                }
            }
        }
        result[0] = begin;
        result[1] = end;
        return result;
    }

}
