package com.fictio.parrot.algorithm;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Greedy.java
 * @Description: 贪心算法
 */
@Slf4j
public class LcGreedy {

    private int[] prices_a = {7,1,5,3,6,4};
    private int[] prices_b = {1,2,3,4,5};
    private int[] prices_c = {7,6,4,3,1};
    private int[] prices_d = {6,1,3,2,4,7};

    @Test
    public void test() {
        log.debug(">>prices={}, maxProfit={}",prices_a,maxProfit(prices_a));
        log.debug(">>prices={}, maxProfit={}",prices_b,maxProfit(prices_b));
        log.debug(">>prices={}, maxProfit={}",prices_c,maxProfit(prices_c));
        log.debug(">>prices={}, maxProfit={}",prices_d,maxProfit(prices_d));
    }

    private int maxProfit(int[] prices) {
        int j = 0;
        int profit = 0, maxProfit = 0;

        for(int i = 1; i < prices.length ; i++) {
            log.debug("i={},j={}",i,j);
            if((prices[i] - prices[j]) > profit) {
                profit = prices[i] - prices[j];
            }else {
                j = i;
                maxProfit += profit;
                profit = 0;
            }
            log.debug("i={},j={},profit={},maxprofit={}",i,j,profit,maxProfit);
            log.debug("--------------------");
        }
        maxProfit += profit;
        return maxProfit;
    }
}
