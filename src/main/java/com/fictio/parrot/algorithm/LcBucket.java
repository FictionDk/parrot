package com.fictio.parrot.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Greedy.java
 * @Description: 遍历
 */
@Slf4j
public class LcBucket {

    private int[] numbers_a = {7,1,5,3,6,4,5};
    private int[] numbers_b = {1,2,3,4,5};
    private int[] numbers_c = {7,6,4,3,1};
    private int[] numbers_d = {6,1,3,2,4,7,7};
    private int[] numbers_e = {1,5,-2,-4,0};

    @Test
    public void test() {
        log.debug(">>numbers={}, containsDuplicate={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_d,containsDuplicate(numbers_d));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_e,containsDuplicate(numbers_e));
    }

    public boolean containsDuplicate(int[] nums) {
        Map<Integer, Set<Integer>> bucketMap = new HashMap<>(20);
        for(int i = 0; i < nums.length; i++) {
            int index = nums[i] % 10;
            Set<Integer> bucket = bucketMap.getOrDefault(index, new HashSet<>());
            if(bucket.contains(nums[i])) return true;
            else {
                bucket.add(nums[i]);
                bucketMap.put(index, bucket);
            }
        }
        return false;
    }
}
