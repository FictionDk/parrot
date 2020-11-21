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

    @Test
    public void test() {
        log.debug(">>prices={}, maxProfit={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>prices={}, maxProfit={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>prices={}, maxProfit={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>prices={}, maxProfit={}",numbers_d,containsDuplicate(numbers_d));
        log.debug(">>a={}, b={}, insert={}",numbers_b,numbers_c,intersect(numbers_b,numbers_c));
    }

    public boolean containsDuplicate(int[] nums) {
        for(int i = 0; i < nums.length; i++) {
            for(int j = i + 1; j < nums.length; j++) {
                if(nums[i] == nums[j]) return true;
            }
        }
        return false;
    }

    public Integer[] intersect(int[] nums1, int[]nums2) {
        List<Integer> container = new ArrayList<>();
        List<Integer> tmp = new ArrayList<>();
        int k = 0;
        boolean isSuccessive = false;
        for(int i = 0; i < nums1.length ; i++) {
            for(int j = k; j < nums2.length; j++) {
                if(nums1[i] == nums2[j]) {
                    tmp.add(nums1[i]);
                    k = j;
                    isSuccessive = true;
                    break;
                }
                isSuccessive = false;
                log.debug("i={},j={},t={},c={}",i,j,tmp,container);
            }
            if(!isSuccessive || k==nums2.length-1) k = 0;
            log.debug("i={},t={},c={}",i,tmp,container);
            if(tmp.size() > container.size()) {
                container.clear();
                container.addAll(tmp);
                tmp.clear();
            }
        }
        return container.toArray(new Integer[container.size()]);
    }
}
