package com.fictio.parrot.algorithm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: InPlace.java
 * @Description: 原地算法
 */
@Slf4j
public class InPlace {

    private int[] sortedNums = {1,1,2,2,2,4,6,6};

    @Test
    public void test() {
        log.debug("deDuplicated={}, len={}",sortedNums,deDuplicated(sortedNums));
    }

    private int deDuplicated(int[] sortedNums) {
        int j = 0;
        log.debug("nums={}",sortedNums);
        for(int i = 1; i < sortedNums.length ; i++) {
            if(sortedNums[j] != sortedNums[i]) {
                sortedNums[++j] = sortedNums[i];
            }
            log.debug("i={}, j={}, nums={}",i,j,sortedNums);
            log.debug("--------------------");
        }
        return j + 1;
    }
}
