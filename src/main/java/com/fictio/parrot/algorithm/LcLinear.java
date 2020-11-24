package com.fictio.parrot.algorithm;

import java.math.BigInteger;
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
    private int[] numbers_j = {0,0};
    private int[] numbers_k = {9,9,9};
    private int[] numbers_l = {9,8,7,6,5,4,3,2,1,0};
    private int[] numbers_m = {6,1,4,5,3,9,0,1,9,5,1,8,6,7,0,5,5,4,3};

    @Test
    public void test() {
        log.debug(">>prices={}, maxProfit={}",numbers_a,containsDuplicate(numbers_a));
        log.debug(">>prices={}, maxProfit={}",numbers_b,containsDuplicate(numbers_b));
        log.debug(">>prices={}, maxProfit={}",numbers_c,containsDuplicate(numbers_c));
        log.debug(">>prices={}, maxProfit={}",numbers_d,containsDuplicate(numbers_d));
        log.debug(">>b={}, c={}, insert={}",numbers_b,numbers_c,intersectOrder(numbers_b,numbers_c));
        log.debug(">>e={}, f={}, insert={}",numbers_e,numbers_f,intersectOrder(numbers_e,numbers_f));
        log.debug(">>g={}, h={}, insert={}",numbers_g,numbers_h,intersectOrder(numbers_g,numbers_h));
        log.debug(">>numbers_f={}, plusOne={}",numbers_f,plusOne(numbers_f));
        log.debug(">>numbers_j={}, plusOne={}",numbers_j,plusOne(numbers_j));
        log.debug(">>numbers_k={}, plusOne={}",numbers_k,plusOne(numbers_k));
        log.debug(">>numbers_l={}, plusOne={}",numbers_l,plusOne(numbers_l));
        log.debug(">>numbers_m={}, plusOne={}",numbers_m,plusOne(numbers_m));
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

    // 通过+1运算,实现数组加1;[4,3,2,1]->[4,3,2,2]  [0,0]->[0,1]
    public int[] plusOneByCalculation(int[] digits) {
        long value = getValue(digits);
        BigInteger bigValue = getBigValue(digits);
        List<Integer> container = buildCollection(++value);
        List<Integer> collection = buildBigCollection(bigValue.add(new BigInteger("1")));
        System.out.println(collection);
        return collectionToArr(container, digits.length);
    }

    // 通过容器,实现数组加1
    public int[] plusOneByCollection(int[] digits) {
        boolean onePlus = true;
        List<Integer> collection = new ArrayList<>();
        for(int i = digits.length - 1; i >= 0; i--) {
            int num = digits[i];
            if(onePlus) num += 1;
            if(num < 10) onePlus = false;
            else num = 0;
            collection.add(num);
        }
        if(onePlus) collection.add(1);
        //Collections.reverse(collection);
        return collectionToArr(collection, digits.length);
    }

    public int[] plusOne(int[] digits) {
        for(int i = digits.length - 1; i >=0; i--) {
            digits[i] ++;
            digits[i] %= 10;
            if(digits[i] != 0) return digits;
        }
        digits = new int[digits.length + 1];
        digits[0] = 1;
        return digits;
    }

    private int[] collectionToArr(List<Integer> collection,int digitsLen) {
        int[] arr = new int[collection.size()>digitsLen?collection.size():digitsLen];
        int i = arr.length - 1;
        for(Integer num : collection) arr[i--] = num;
        return arr;
    }

    private List<Integer> buildBigCollection(BigInteger val){
        List<Integer> collection = new ArrayList<>();
        for(char c : val.toString().toCharArray())  collection.add(Integer.parseInt(String.valueOf(c)));
        return collection;
    }

    private List<Integer> buildCollection(long value){
        long remainder = 10;
        List<Integer> container = new ArrayList<>();
        while(value != value % remainder) {
            //log.debug("r={},v={},c={},v%r={}",remainder,value,container,value%remainder);
            long num = value % remainder;
            value = value - num;
            num = num / (remainder/10);
            container.add((int) num);
            remainder = remainder * 10;
        }
        container.add((int) (value/(remainder/10)));
        return container;
    }

    private long getValue(int[] digits) {
        long remainder = (long) Math.pow(10, (digits.length-1));
        long value = 0;
        for(int num : digits) {
            value = num * remainder + value;
            remainder /= 10;
        }
        return value;
    }

    private BigInteger getBigValue(int[] digits) {
        StringBuilder builder = new StringBuilder();
        for(int num : digits) {
            builder.append(num);
        }
        return new BigInteger(builder.toString());
    }
}
