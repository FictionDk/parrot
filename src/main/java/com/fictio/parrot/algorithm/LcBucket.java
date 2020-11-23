package com.fictio.parrot.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: LcBucket.java
 * @Description: 桶
 */
@Slf4j
public class LcBucket {

    private int[] numbers_a = {7,1,5,3,6,4,5};
    private int[] numbers_b = {1,2,3,4,5};
    private int[] numbers_c = {7,6,4,3,1};
    private int[] numbers_d = {6,1,3,2,4,7,7};
    private int[] numbers_e = {1,5,-2,-4,0};
    private int[] numbers_f = {4,1,2,1,2};
    private int[] numbers_g = {4,9,5};
    private int[] numbers_h = {9,4,9,8,4};
    private int[] numbers_j = {1,2,2,1};
    private int[] numbers_k = {2,2};


    @Test
    public void test() {
        log.debug(">>numbers={}, containsDuplicate={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_d,containsDuplicate(numbers_d));
        log.debug(">>numbers={}, containsDuplicate={}",numbers_e,containsDuplicate(numbers_e));
        log.debug(">>numbers={}, singleNumber={}",numbers_f,singleNumber(numbers_f));
        log.debug(">>a={}, b={}, intersect={}",numbers_a,numbers_b,intersect(numbers_a,numbers_b));
        log.debug(">>g={}, h={}, intersect={}",numbers_g,numbers_h,intersect(numbers_g,numbers_h));
        log.debug(">>j={}, k={}, intersect={}",numbers_j,numbers_k,intersect(numbers_j,numbers_k));
    }

    // 判断是否存在重复元素
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

    // 找出那个只出现了一次的元素
    public int singleNumber(int[] nums) {
        Set<Integer> bucketSet = new HashSet<>(nums.length/2 + 1);
        for(int num : nums) {
            if(bucketSet.contains(num)) bucketSet.remove(num);
            else bucketSet.add(num);
        }
        return (int) bucketSet.toArray()[0];
    }

    // 找出两个集合的无序交集
    public int[] intersect(int[] nums1, int[]nums2) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> hashTable = new HashMap<>();
        for(int num : nums1) {
            int count = hashTable.getOrDefault(num, 0);
            hashTable.put(num, ++count);
        }
        log.debug("HashTable={}",hashTable);
        for(int num : nums2) {
            int count = hashTable.getOrDefault(num, 0);
            if(count > 0) {
                hashTable.put(num, --count);
                result.add(num);
            }
        }
        int[] arr = new int[result.size()];
        int i = 0;
        for(Integer number : result) arr[i++] = number;
        return arr;
    }

}
