package com.fictio.parrot.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * Longest Substring Without Repeating Characters
 *
 */
@Slf4j
public class LeetCode03 {

   /**
    * Input: "pwwkew"
    * Output: 3
    * Explanation: The answer is "wke", with the length of 3. 
    * Note that the answer must be a substring, "pwke" is a subsequence and not a substring. 
    */
    @Test
    public void test() {
        String test = "pwwkew";
        //test = "abcabcbb";
        //test = "bbbbb";
        //test = "dvdf";
        //test = "anviaj";
        int maxLen = lengthOfLongestSubstring(test);
        log.info("maxLen = {}",maxLen);
    }
    
    private class CharSub {
        Character brokenChar = null;
        boolean isBroken = false;
        public boolean needTempListAdd(char c) {
            return isBroken && brokenChar != null && (!brokenChar.equals(c));
        }
        @Override
        public String toString() {
            return "CharSub [brokenChar=" + brokenChar + ", isBroken=" + isBroken + "]";
        }
    }
    
    public int lengthOfLongestSubstring(String s) {
        int maxLen = 0;
        List<Character> chList = new ArrayList<>();
        List<Character> tmpList = new ArrayList<>();
        CharSub cs = new CharSub();
        
        char[] chs = s.toCharArray();
        for(int i = 0; i < chs.length; i++) {
            log.info("i = {} ,chList = {}; tmpList = {}; cs = {}",i,chList,tmpList,cs);
            addChar(chs[i],chList,tmpList,cs);
        }
        maxLen = chList.size();
        return maxLen;
    }
    
    private void addChar(char c,List<Character> chList ,List<Character> tmpList, 
            CharSub cs) {
        
        log.info("Condition 0: c={},isBroken={},brokenCh={}",c,cs.isBroken,cs.brokenChar);
        
        log.info("Condition 1: {}",cs.needTempListAdd(c));
        
        if((!tmpList.contains(c)) && cs.needTempListAdd(c)) {
            addToList(c, tmpList);
        }
        
        log.info("Condition 2: {}",(tmpList.size() > chList.size()));
        
        if(tmpList.size() > chList.size()) {
            reBuild(tmpList,chList);
            cs.isBroken = false;
            return;
        }
        
        log.info("Condition 3: {}",(chList.contains(c)));
        if(chList.contains(c)) {
            cs.isBroken = true;
            cs.brokenChar = c;
            if(tmpList.isEmpty()) tmpListInit(chList,tmpList,chList.get(chList.size()-1));
            addToList(c,tmpList);
        }else if(!cs.isBroken) {
            addToList(c,chList);
        }
    }
    
    /**
     * <p> 对于chList需要倒着取(先进后出)
     * 
     * @param chList
     * @param tmpList
     * @param c
     */
    private void tmpListInit(List<Character> chList,List<Character> tmpList,char c) {
        log.debug("=======");
        log.debug("tmpList={},chList={},c",tmpList,chList,c);
        Iterator<Character> ite = chList.iterator();
        Character lastCh = null;
        while(ite.hasNext()) {
            char ch = ite.next();
            log.debug(">>>>> ch={},c={},lastCh={}",ch,c,lastCh);
            if(lastCh != null && lastCh.equals(ch)) break;
            if(!tmpList.contains(ch)) 
                tmpList.add(ch);
            else break;
            lastCh = ch;
            log.debug("> ch={},c={},lastCh={}",ch,c,lastCh);
        }
        addToList(c,tmpList);
        log.debug("tmpList={},chList={},c",tmpList,chList,c);
        log.debug("=======");
    }
    
    private void addToList(char c, List<Character> chList) {
        if(!chList.contains(c)) {
            chList.add(c);
        }
    }
    
    /**
     * <p> 清空currentList内容
     * <p> 将tmpList转移到currentList
     * <p> 清空oldList
     * 
     * @param oldList
     * @param newList
     */
    private void reBuild(List<Character> tmpList, List<Character> currentList) {
        currentList.clear();
        currentList.addAll(tmpList);
        tmpList.clear();
    }
    
    @Test
    public void swapTest() {
        List<Character> listA = getSetByString("IGOT");
        List<Character> listB = getSetByString("HARD");
        swap(listA, listB);
        log.info("listA: {}",listA);
        log.info("listB: {}",listB);
    }
    
    private List<Character> getSetByString(String str){
        List<Character> chlist = new ArrayList<>();
        char[] chs = str.toCharArray();
        for(int i = 0; i < chs.length; i++) {
            chlist.add(chs[i]);
        }       
        return chlist;
    }
    
    public void swap(List<Character> listA ,List<Character> listB) {
        List<Character> listTmp = new ArrayList<>();
        listTmp.addAll(listB);
        listB.clear();
        listB.addAll(listA);
        listA.clear();
        listA.addAll(listTmp);
    }
}
