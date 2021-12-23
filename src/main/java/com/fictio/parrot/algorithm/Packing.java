package com.fictio.parrot.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

/**
 * 背包问题
 */
@Slf4j
public class Packing {

    private final int[] weights = {10,10,10,15,20,20,20,20};  // 体积
    private final int[] values = {1,1,3,3,3,3,5,5};   // 价值
    // 固定背包容量65,共8件物品,每件物品可选择放也可以选择不放
    @Test public void test1(){
        int num = values.length;
        int total = 65;
        int[][] dp = new int[weights.length+1][total+1];

        for(int i = 1; i <= num; i++){
            int w = weights[i-1], v = values[i-1];
            log.debug("i={},w体积={},v价值={}",i,w,v);
            for(int j = 1; j <= total; j++){
                if(j >= w){
                    log.debug("i={},j={},不放:{},放:{}",i,j,dp[i-1][j],dp[i-1][j-w]+v);
                    dp[i][j] = Math.max(dp[i-1][j], dp[i-1][j-w]+v);
                }else{
                    log.debug("i={},j={},不放价值:{}",i,j,dp[i-1][j]);
                    dp[i][j] = dp[i-1][j];
                }
            }
        }
        log.debug("dp={}", Arrays.deepToString(dp));
        log.debug("dp={}", dp[num][total]); // 最大价值
    }

}
