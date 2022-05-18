package com.fictio.parrot.book.algs4.l01;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Base {
    // 二分查找的递归实现
    @Test
    public void mid(){
        int[] arr = {1, 6, 8, 10, 11, 50, 99};
        log.debug("rank_1={}",rank(11, arr));
        log.debug("rank_2={}",rank(44, arr));
        log.debug("rank_3={}",rank(99, arr));
        log.debug("rank_4={}",rank(1, arr));
    }

    public int rank(int key, int[] arr){
        return rank(key, arr,0, arr.length - 1);
    }

    private int rank(int key, int[] arr, int lo, int hi){
        int mid = lo + (hi - lo) / 2;  // 找中
        if(arr[mid] == key) return mid;  // 如果刚好中间,返回
        if(lo >= hi) return -1;  // 如果头大于尾,结束返回(边界处理)
        log.debug("lo={},hi={},mid={}",lo, hi, mid);
        if(key < arr[mid]) return rank(key, arr, lo, mid);  // 如果在中的左边
        else if(key > arr[mid]) return rank(key, arr, mid + 1, hi);  // 如果在右边
        else return mid;
    }
}
