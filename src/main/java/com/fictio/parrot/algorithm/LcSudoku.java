package com.fictio.parrot.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LcSudoku {
    enum Type {ROW,COLUMN,BLOCK;}

    private char[][] board = new char[][] {
         {'5','3','.','.','7','.','.','.','.'},
         {'6','.','.','1','9','5','.','.','.'},
         {'.','9','8','.','.','.','.','6','.'},
         {'8','.','.','.','6','.','.','.','3'},
         {'4','.','.','8','.','3','.','.','1'},
         {'7','.','.','.','2','.','.','.','6'},
         {'.','6','.','.','.','.','2','8','.'},
         {'.','.','.','4','1','9','.','.','5'},
         {'.','.','.','.','8','.','.','7','9'}
    };

    @Test
    public void tests() {
        log.debug("{}",isValidSudoku(board));
    }

    public boolean isValidSudoku(char[][] board) {
        Map<Integer,Set<Integer>> colMap = new HashMap<>();
        Map<Integer,Set<Integer>> blockMap = null;
        for(int row = 0; row < 9; row++) {
            Set<Integer> rowSet = new HashSet<>();
            if(row%3 == 0) blockMap  = new HashMap<>();
            for(int col = 0; col < 9; col++) {
                char c = board[row][col];
                if('.' == c) continue;
                int val = Integer.valueOf(String.valueOf(c));

                boolean result = rowSet.add(val);
                if(!result) return false;

                Set<Integer> colSet = colMap.getOrDefault(col, new HashSet<>());
                result = colSet.add(val);
                if(!result) return false;
                colMap.put(col, colSet);

                int blockIndex = col / 3;
                Set<Integer> blockSet = blockMap.getOrDefault(blockIndex, new HashSet<>());
                result = blockSet.add(val);
                if(!result) return false;
                blockMap.put(blockIndex, blockSet);
            }

        }
        return true;
    }
}
