package com.fictio.parrot.demo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

class Base {
    private String baseName = "base";
    public void callName () {
        System.out.println(baseName);
    }
    public Base () {callName();}
    static class Sub extends Base {
        private String baseName = "sub";
        public void className() {
            System.out.println(baseName);
        }
    }
}

public class JavaBaseTest {
    
    @Test
    public void sum() {
        int sum = 0;
        for(int i = 0; i<=5; i++) sum +=i;
        System.out.println(sum);
    }
    
    @Test
    public void equ() {
        int i = 0;
        Integer j = new Integer(0);
        System.out.println(i == j);
        System.out.println(j.equals(i));
    }
    
    @Test
    public void ext() {
        new Base();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void lis() {
        List listA = new ArrayList<>();
        listA.add(0);
        List listB = listA;
        System.out.println(listA.get(0) instanceof Integer);
        System.out.println(listB.get(0) instanceof Integer);
    }
    
    class Example {
        String str = new String("hello");
        char[] ch = {'a','b'};
        public void change(String str,char ch []) {
            str = "test ok";
            ch[0] = 'c';
        }
    }
    
    @Test
    public void tes() {
        Example exp = new Example();
        exp.change(exp.str,exp.ch);
        System.out.print(exp.str +" and ");
        System.out.println(exp.ch);
    }

}
