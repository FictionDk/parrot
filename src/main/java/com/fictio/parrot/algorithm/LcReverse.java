package com.fictio.parrot.algorithm;

import java.math.BigInteger;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LcReverse {
    char[] arr1 = {'h','e','l','o'};
    char[] arr2 = {'h','e','l','l','o'};
    private char[] arr3 = {' '};

    @Test public void tests() {
        reverseStr(arr3); log.debug("{}",arr3);
        reverseInt(-1120L);
        reverseInt(112000L);
        reverseInt(10012000L);
        reverseInt(-1L);
        reverseInt(0L);
        reverseInt(2147483648L);
        log.debug("-->{}",reverseInt(9646324351L));
        log.debug("-->{}",reverseInt(1534236469L));
    }

    /**
     * <p> 字符串反转,使用原地算法,双指针;
     * <p> https://leetcode-cn.com/leetbook/read/top-interview-questions-easy/xnhbqj/
     */
    private void reverseStr(char[] s) {
        for(int i = 0, j = s.length -1; i <= j; i++, j--) {
            char t = s[i];
            s[i] = s[j];
            s[j] = t;
        }
    }

    /**
     * <p> https://leetcode-cn.com/leetbook/read/top-interview-questions-easy/xnx13t/
     */
    private int reverseInt(Long input) {
        boolean isZero = true;
        StringBuilder sb = new StringBuilder();
        if(input < 0) sb.append("-");
        while(input != 0) {
            long[] result = getMantissa(input);
            input = result[1];
            if(isZero && result[0] == 0) continue;
            else isZero = false;
            sb.append(result[0]);
        }
        if(sb.length() == 0) sb.append(0);
        log.debug("{}",Long.parseLong(sb.toString()));
        log.debug("{}",new BigInteger(sb.toString()));
        log.debug("{}",new BigInteger(sb.toString()).intValue());
        log.debug("----------------------");
        int num = 0;
        try {
            num = Integer.valueOf(sb.toString());
        } catch (NumberFormatException  e) {
            num = 0;
        }
        return num;
    }

    // 参数为值传递
    private long[] getMantissa(Long raw) {
        long mantissa = raw % 10;
        long value = raw / 10;
        log.debug("{} - {}", value, mantissa);
        return new long[]{mantissa < 0 ? - mantissa : mantissa, value};
    }
}
