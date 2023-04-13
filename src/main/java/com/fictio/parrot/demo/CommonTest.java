package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonTest {
    private static class Stu {
        String name;
        int age;
        LocalDate birthday;
        public Stu(String name,int age,LocalDate birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }
        @Override
        public boolean equals(Object obj){
            if(this == obj) return true;
            if(obj == null) return false;
            if(obj instanceof Stu){
                Stu stu = (Stu) obj;
                return this.name.equals(stu.name);
            }
            return super.equals(obj);
        }
        @Override
        public int hashCode(){
            return (name==null)?0:name.hashCode();
        }
        @Override
        public String toString() {
            return "Stu [name=" +
                    name +
                    ", age=" +
                    age +
                    ", birthday=" +
                    birthday.format(DateTimeFormatter.ISO_DATE) +
                    "]";
        }

    }

    @Test
    public void removeCollection(){
        List<Stu> stuList = Arrays.asList(
                new Stu("E",8,LocalDate.ofYearDay(1999, 20)),
                new Stu("B",2,LocalDate.ofYearDay(2000, 101)),
                new Stu("C",3,LocalDate.ofYearDay(1999, 101)),
                new Stu("A",3,LocalDate.ofYearDay(1998, 101)));
        Set<Stu> stuSet = new HashSet<>(stuList);
        stuSet.add(null);
        log.info("SetInit={}",stuSet);
        stuSet.remove(null);
        stuSet.remove(new Stu("E",3,LocalDate.ofYearDay(2000, 101)));
        log.info("SetRemove={}",stuSet);
        log.info("{}",stuList.stream().collect(Collectors.toMap(s->s.name, s->s.age)));
    }
    
    @Test
    public void addAndSortCollection() {
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
        Comparator<Stu> stuCompare = (o1, o2) -> {
            return o1.birthday.compareTo(o2.birthday);
        };
        stus.sort(stuCompare);
        Stu s = stus.get(0);
        s.age = 5;
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
    public void localDateCompare() {
        LocalDate dt2 = LocalDate.now().plusDays(-3);
        LocalDate dt3 = LocalDate.now();
        System.out.println(dt2 +" is after "+ dt3 +dt2.isAfter(dt3));
    }
    
    @Test
    public void finallyTest() {
        int x = addTest();
        System.out.println(x);
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
    
    static class Example extends Thread {
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s-%s%n","run",Thread.currentThread().getName());
        }
    }

    @SuppressWarnings("all")
    @SneakyThrows
    @Test
    public void differenceBetweenStartAndRun() {
        Example ep = new Example();
        ep.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.printf("%s,%s",t.getName(),e.toString());
            }
        });
        ep.run(); // 可直接执行,但实际是由当前线程执行(main),不会启动新线程
        ep.start(); // 启动线程,然后执行1次run new->runnable->running->dead
        //ep.start(); // 不能重复启动-IllegalThreadStateException
        ep.run(); // run可重复执行
        Thread.sleep(500); // 线程不会立即销毁
        System.out.printf("isAlive=%s%n",ep.isAlive()); // 线程run结束后线程被JVM回收和销毁
        Thread.sleep(1000);
    }
    
    @Test
    public void syncTest() {
        final Object obj = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                try {
                    obj.wait();
                    System.out.println("thread 1 wake up");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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
}
