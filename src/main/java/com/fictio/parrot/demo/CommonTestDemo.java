package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonTestDemo {
    
    private class Stu {
        String name;
        int age;
        LocalDate birthday;
        public Stu(String name,int age,LocalDate birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Stu [name=");
            builder.append(name);
            builder.append(", age=");
            builder.append(age);
            builder.append(", birthday=");
            builder.append(birthday.format(DateTimeFormatter.ISO_DATE));
            builder.append("]");
            return builder.toString();
        }

    }
    
    @Test
    public void setCollectonTest() {
        Set<String> sets = new HashSet<>();
        log.info("add A {}",sets.add("A"));
        log.info("add B {}",sets.add("B"));
        log.info("add a {}",sets.add("a"));
        log.info("add A {}",sets.add("A"));
        
        List<String> list = Arrays.asList("A","B","C","D");
        list.sort(Comparator.reverseOrder());
        log.info("list {}",list);
        
        List<Stu> stus = Arrays.asList(
                new Stu("E",8,LocalDate.ofYearDay(1999, 20)),
                new Stu("B",2,LocalDate.ofYearDay(2000, 101)),
                new Stu("C",3,LocalDate.ofYearDay(1999, 101)),
                new Stu("A",3,LocalDate.ofYearDay(1998, 101)));
        Comparator<Stu> stucompare = new Comparator<Stu>() {
            @Override
            public int compare(Stu o1, Stu o2) {
                //return o1.age - o2.age;
                return o1.birthday.compareTo(o2.birthday);
            }
        };
        stus.sort(stucompare);
        log.info("stus {}",stus);
    }
    
    @Test
    public void strTest() {
        String path = "/tmp/tmp_1.pdf";
        String[] paths = path.split("\\.");
        log.info("paths len {}",paths.length);
        for(String p : paths) log.info("{}",p);
        paths[1] = "html";
        log.info("new path {}",paths[0]+"."+paths[1]);
    }
    
    @Test
    public void test() {
        LocalDateTime dt2 = LocalDateTime.now().plusDays(-3);
        LocalDateTime dt3 = LocalDateTime.now();
        System.out.println(dt2.toString()+" is after "+dt3.toString()+dt2.isAfter(dt3));
    }
    
    @Test
    public void finallyTest() {
        int x = addTest();
        System.out.println(x);
    }
    
    static class Example extends Thread {
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("run");
        }
    }
    
    @Test
    public void theadTest() {
        Example ep = new Example();
        ep.run();
        System.out.println("main");
    }
    
    @Test
    public void syncTest() {
        final Object obj = new Object();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                synchronized (obj) {
                    try {
                        obj.wait();
                        System.out.println("thread 1 wake up");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread() {
            @Override
            public void run() {
                synchronized (obj) {
                    obj.notifyAll();
                    System.out.println("t2 send notify ");
                };
            }
        };
        t2.start();
    }
    
    public int addTest() {
        int x = 1;
        try {
            x++;
            //先将待return的结果缓存,再执行finally
            return x;
        } finally {
            x++;
            //如果有return,覆盖try中的return
            //return x;
        }
    }
    
}
