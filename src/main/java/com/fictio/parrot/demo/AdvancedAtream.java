package com.fictio.parrot.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdvancedAtream {
    
    private class Student {
        String name;
        int age;
        String idNum;
        public Student(String name,int age,String idNum) {
            this.name = name;
            this.age = age;
            this.idNum = idNum;
        }
    }
    
    private List<Student> stus = Arrays.asList(
       new Student("张三", 13, "9949"),
       new Student("李四", 13, "9950"),
       new Student("王五", 13, "9951"),
       new Student("马六", 13, "9950"),
       new Student("刘七", 13, "9950")
            );
    
    @Test
    public void test1() {
        Map<String,Student> stuMap = stus.stream().collect(
                Collectors.toMap(k->k.name, v->v));
        
        stuMap.forEach((k,v)->{
            log.info("{} - {}",k,v);
        });
        
    }
    
    

}
