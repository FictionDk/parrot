package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.google.common.base.Joiner;

class Base {
    String testName;
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

    private final LocalDate NOW = LocalDate.now();

    private final long longdat = 100L;

    @Test
    public void optionalTest() {
        System.out.println(Optional.ofNullable(new Base()).map(b->b.testName == null ? "" : b.testName));
    }

    @Test
    public void finalTest() {
        System.out.println(NOW.plusDays(-1));
        System.out.println(longdat + 1);
        System.out.println(NOW);
    }

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
        String [] strArray = {"test","test01"};
        String arrStr = Arrays.toString(strArray);
        System.out.println(arrStr);
        String [] arr = arrStr.split(",");
        System.out.println(arr.toString());
        multParms("go ","to ","the ","school!");
    }

    private void multParms(String... args) {
        System.out.println(Joiner.on("").join(args).toString());
    }


    @Test
    public void arrComparison() {
        double[] a = {5.0, 7.0};
        System.out.println(a.length);
        for(int i = 2; i < 9; i++) System.out.println(i + ":" +outofRange(a, i));
    }

    private boolean outofRange(double[] arr, int val) {
        if(arr.length < 2) throw new RuntimeException("arr size err");
        return val - arr[0] < 0 || arr[1] - val < 0;
    }
}
