package com.fictio.parrot.algorithm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: InPlace.java
 * @Description: 原地算法
 */
@Slf4j
public class LcInPlace {

    private int[] sortedNums = {1,1,2,2,2,4,6,6};
    private int[] randomNums = {1,4,9,10,11,-5,-1};

    @Test
    public void test() {
        log.debug("deDuplicated={}, len={}",sortedNums,deDuplicated(sortedNums));
        for(int i = 0; i < randomNums.length; i++) {
            log.debug("rotate(k={})={}",i, rotate(randomNums.clone(), i));
        }
        int[] arrB = {1,2,4,5};
        log.debug("rotate(k={})={}",6, rotate(arrB, 6));
        int[] arrA = {1,2,3,4,5,6};
        log.debug("rotate(k={})={}",4, rotate(arrA, 4));
    }

    // 使用'原地算法'删除数组中重复项
    public int deDuplicated(int[] sortedNums) {
        int j = 0;
        log.debug("nums={}",sortedNums);
        for(int i = 1; i < sortedNums.length ; i++) {
            if(sortedNums[j] != sortedNums[i]) {
                sortedNums[++j] = sortedNums[i];
            }
            //log.debug("i={}, j={}, nums={}",i,j,sortedNums);
            //log.debug("--------------------");
        }
        return j + 1;
    }

    // 使用'原地算法'将数组旋转k次
    public int[] rotate(int[] nums,int k) {
        if(k >= nums.length) k = k % nums.length;
        reverse(0, nums.length-1, nums);
        reverse(0, k-1, nums);
        reverse(k, nums.length-1, nums);
        return nums;
    }

    private void reverse(int start, int end, int[] nums) {
        while(start < end) {
            int t = nums[start];
            nums[start] = nums[end];
            nums[end] = t;
            //log.debug("start={}, end={}, nums={}",start,end,nums);
            start ++;
            end --;
        }
    }
}
