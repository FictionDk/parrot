package com.fictio.parrot.algorithm;

import java.util.ArrayList;
import java.util.List;

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
    private int[] numbers_c = {7,6,4,5,3,1};
    private int[] numbers_d = {6,1,3,2,4,7,7};
    private int[] numbers_e = {4,9,5};
    private int[] numbers_f = {9,4,9,8,4};
    private int[] numbers_g = {1,2,2,1};
    private int[] numbers_h = {2,2};

    @Test
    public void test() {
        log.debug(">>prices={}, maxProfit={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>prices={}, maxProfit={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>prices={}, maxProfit={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>prices={}, maxProfit={}",numbers_d,containsDuplicate(numbers_d));
        log.debug(">>b={}, c={}, insert={}",numbers_b,numbers_c,intersectOrder(numbers_b,numbers_c));
        log.debug(">>e={}, f={}, insert={}",numbers_e,numbers_f,intersectOrder(numbers_e,numbers_f));
        log.debug(">>g={}, h={}, insert={}",numbers_g,numbers_h,intersectOrder(numbers_g,numbers_h));
    }

    public boolean containsDuplicate(int[] nums) {
        for(int i = 0; i < nums.length; i++) {
            for(int j = i + 1; j < nums.length; j++) {
                if(nums[i] == nums[j]) return true;
            }
        }
        return false;
    }

    // 找出有序的交集
    public int[] intersectOrder(int[] nums1, int[]nums2) {
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < nums1.length; i ++) {
            for(int j = 0; j < nums2.length; j++) {
                List<Integer> temp = new ArrayList<>();
                if(nums1[i] == nums2[j]) {
                    for(int k = 0; i + k < nums1.length && j + k < nums2.length ; k++) {
                        if(nums2[j+k] == nums1[i+k]) {
                            temp.add(nums2[j+k]);
                        }else {
                            break;
                        }
                    }
                }
                log.debug("i={},j={},temp={},result={}",i,j,temp,result);
                if(temp.size() > result.size()) {
                    result.clear();
                    result.addAll(temp);
                }
            }
        }
        int[] arr = new int[result.size()];
        int i = 0;
        for(Integer number : result) {
            arr[i++] = number;
        }
        return arr;
    }
}
