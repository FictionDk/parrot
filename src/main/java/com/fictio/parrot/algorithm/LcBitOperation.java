package com.fictio.parrot.algorithm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Title: 位运算
 * Description:
 */
@Slf4j
public class LcBitOperation {
    private int[] numbers_f = {4,1,2,1,2};

    @Test
    public void test() {
        log.debug(">>numbers={}, singleNumber={}",numbers_f,singleNumber(numbers_f));
    }

    /**
     * <p> 找出那个只出现了一次的元素
     * <p> 1. 任何数和0异位运算,返回自己
     * <p> 2. 任何数和其自身做异或运算，结果是 0，即 a ⊕ a=0
     * <p> 3. 异或运算满足交换律和结合律，即 a⊕b⊕a=b⊕a⊕a=b⊕(a⊕a)=b⊕0=b。
     */
    public int singleNumber(int[] nums) {
        int single = 0;
        for(int num : nums) {
            log.debug("n={},befor s={}",num,single);
            single ^= num;
            log.debug("n={},after s={}",num,single);
        }
        return single;
    }
}
