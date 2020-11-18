package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.fictio.parrot.demo.Student.Sex;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
class Student {
    public enum Sex  {
        MALE,FEMALE
    }
    public Student(String name,Sex sex,LocalDate birth) {
        this.name = name;
        this.sex = sex;
        this.birthday = birth;
    }
    public Student setGrade(Integer g) {
        this.grade = g;
        return this;
    }
    String name;
    Sex sex;
    LocalDate birthday;
    Integer grade;
}

@Slf4j
public class CollectionTest {

    private List<Student> stus;

    @Before
    public void collectionsBuild() {
        stus = new ArrayList<>();
        stus.add(new Student("张三",Student.Sex.MALE,LocalDate.of(2000, 1, 1)).setGrade(90));
        stus.add(new Student("李四",Student.Sex.FEMALE,LocalDate.of(1990, 1, 1)).setGrade(80));
        stus.add(new Student("李四",Student.Sex.MALE,LocalDate.of(1993, 1, 1)).setGrade(55));
        stus.add(new Student("王五",Student.Sex.MALE,LocalDate.of(1991, 1, 1)).setGrade(34));
        stus.add(new Student("金唯",Student.Sex.FEMALE,LocalDate.of(1992, 1, 1)).setGrade(60));
        //stus.add(new Student("金唯",Student.Sex.FEMALE,null));
    }

    @Test
    public void sortTest() {
        if(stus == null || stus.isEmpty()) return;
        log.debug("{}",stus.stream().sorted(Comparator.comparing(Student::getGrade)).mapToInt(Student::getGrade).findFirst());
        stus = stus.stream().sorted(Comparator.comparing(Student::getGrade).thenComparing(Student::getSex))
                .collect(Collectors.toList());
        stus.forEach(s->log.info("{}",s));
    }

    @Test
    public void distinctTest() {
        if(stus == null || stus.isEmpty()) return;
        stus = stus.stream().filter(distinctByKey(s->(s.getName()+s.getBirthday())))
                .collect(Collectors.toList());
        stus.forEach(s->log.info("{}",s));
    }

    @Test
    public void sortAndDistinctJoin() {
        if(stus == null || stus.isEmpty()) return;
        stus = stus.stream().sorted(Comparator.comparing(Student::getBirthday).thenComparing(Student::getSex).reversed())
                .filter(distinctByKey(s->(s.getName())))
                .collect(Collectors.toList());
        stus.forEach(s->log.info("{}",s));
    }

    @Test
    public void group() {
        if(stus == null || stus.isEmpty()) return;
        Map<Sex,List<Student>> stuMap = stus.stream().collect(Collectors.groupingBy(Student::getSex));
        stuMap.forEach((k,v)->log.info("{} | {}",k,v));
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor){
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t->map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Test
    public void foreachTest() {
        for(Student s : stus) {
            if("张三".equals(s.getName())) s.setBirthday(s.getBirthday().plusMonths(2));
        }
        stus.stream().forEach(System.out::println);
    }

}
