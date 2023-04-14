package com.fictio.parrot.demo;

import java.math.BigInteger;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("all")
@Slf4j
public class LeetCode02 {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int x){val = x;}
        @Override
        public String toString() {
            return "ListNode [val=" + val + ", next=" + next + "]";
        }
    }

    private ListNode addNode(ListNode ld, int val) {
        ListNode newLd = new ListNode(val);
        if(ld != null) 
            newLd.next = ld;
        return newLd;
    }
    
    private ListNode preData(int[] nums) {
        ListNode tmp = null;
        for(int i = 0; i < nums.length; i++) {
            tmp = addNode(tmp,nums[i]);
        }
        return tmp;
    }
    
    @SuppressWarnings("unused")
    private void listNodePrint(ListNode ld) {
        while(ld != null) {
            log.info(": {}",ld);
            ld = ld.next;
        }
    }
    
    /**
     * Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
     * Output: 7 -> 0 -> 8
     * Explanation: 342 + 465 = 807.
     *
     * [3, 4, 2]
     * [4, 6, 5]
     *
     * [9]
     * [1,9,9,9,9,9,9,9]
     * 
     * [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1]
     * 
     * You are given two non-empty linked lists representing two non-negative integers. \
     * The digits are stored in reverse order and each of their nodes contain a single digit. 
     * Add the two numbers and return it as a linked list.
     * You may assume the two numbers do not contain any leading zero, except the number 0 itself.
     */
    @Test
    public void twoSumTest() {
        //int [] nums1 = {3, 4, 2};
        //int [] nums2 = {4, 6, 5};
        int [] nums1 = { 3, 4, 2 };
        int [] nums2 = { 4, 6, 5 };
        ListNode ld1 = preData(nums1);
        ListNode ld2 = preData(nums2);
        
        ListNode ld3 = addTwoNumbers(ld1, ld2);
        log.info("{}",ld3);
    }
    
    private ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        BigInteger num1 = getNumber(l1);
        BigInteger num2 = getNumber(l2);
        BigInteger num3 = num1.add(num2);
        log.info("{} + {} = {}",num1,num2,num3);
        return numberToNode(num1.add(num2));
    }
    
    private ListNode numberToNode(BigInteger val) {
        if(BigInteger.valueOf(0L).equals(val)) return new ListNode(0);
        else {
            ListNode tmp = null;
            String valStr = String.valueOf(val);
            for(int i = 0; i < valStr.length() ; i++) {
                char ch = valStr.charAt(i);
                tmp = addNode(tmp, Character.getNumericValue(ch));
            }
            return tmp;
        }
    }
    
    private BigInteger getNumber(ListNode ld) {
        BigInteger result = BigInteger.valueOf(0);
        BigInteger count = BigInteger.valueOf(1);
        while(ld != null) {
            result = result.add(BigInteger.valueOf(Long.parseLong(String.valueOf(ld.val)))
                    .multiply(count));
            ld = ld.next;
            count = count.multiply(BigInteger.valueOf(10L));
            log.info("result: {}, count: {}",result, count);
        }
        return result;
    }
    
    
}
