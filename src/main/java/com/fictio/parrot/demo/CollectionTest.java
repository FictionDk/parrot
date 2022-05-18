package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Student(Integer id,String name,Sex sex,LocalDate birth) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.birthday = birth;
    }
    Integer id;
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
        stus.add(new Student(1,"张三",Student.Sex.MALE,LocalDate.of(2000, 1, 1)));
        stus.add(new Student(4,"李四",Student.Sex.FEMALE,LocalDate.of(1990, 1, 1)));
        stus.add(new Student(3,"李四",Student.Sex.MALE,LocalDate.of(1993, 1, 1)));
        stus.add(new Student(null,"王五",Student.Sex.MALE,LocalDate.of(1991, 1, 1)));
        stus.add(new Student(2,"金唯",Student.Sex.FEMALE,LocalDate.of(1992, 1, 1)));
        stus.add(new Student(0,"金唯",Student.Sex.FEMALE,null));
    }

    @Test
    public void sortTest() {
        Vector<Student> stuVec = new Vector<>();
        stuVec.add(stus.get(2));
        stus = Collections.synchronizedList(stus);
        new CopyOnWriteArrayList<>(stus);

        Stream.iterate(0, i->i+1).limit(stus.size()).forEach(index->{
            log.debug("Stus[{}] = {}", index, stus.get(index));
        });

        if(stus == null || stus.isEmpty()) return;
        stus = stus.stream().sorted(Comparator.comparing(Student::getBirthday).thenComparing(Student::getSex))
                .collect(Collectors.toList());
        stus.forEach(s->log.info("{}",s));
    }

    @Test
    public void sortNullTest() {
        stus.stream().sorted(Comparator.comparing(Student::getId, Comparator.nullsLast(Integer::compareTo)))
        .collect(Collectors.toList()).forEach(s->log.info("{}",s));

        stus.stream().sorted(Comparator.comparing(Student::getId, Comparator.nullsFirst(Integer::compareTo))
                .reversed())
        .collect(Collectors.toList()).forEach(s->log.info("{}",s));
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
    public void listAddTest() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 20 ; i++) {
            list.add(1);
        }
    }

    /**
     * Sex.FEMALE.equals(val.getSex()) || Sex.MALE.equals(val.getSex())
     */
    @Test
    public void streamMatchTest() {
        log.info("1.Match result = {}",stus.stream().allMatch(val->val.getBirthday().isAfter(LocalDate.now())));
        log.info("2.Match result = {}",stus.stream().filter(val-> val.getBirthday() != null)
                .allMatch(val->val.getBirthday().isBefore(LocalDate.now())));
        log.info("3.Match result = {}",stus.stream().anyMatch(val->val.getName().equals("大侠")));
        log.info("4.Match result = {}",stus.stream().anyMatch(val->val.getName().equals("张三")));
        log.info("5.Match result = {}",stus.stream().allMatch(val->val.getName().equals("张三")));
        Student stu = stus.get(0);
        log.info("51={}",Sex.FEMALE.equals(stu.getSex()) || Sex.MALE.equals(stu.getSex()));
        log.info("6.Match result = {}",stus.stream().allMatch(val->(Sex.FEMALE.equals(val.getSex())
                || Sex.MALE.equals(val.getSex()))));
    }

    @Test
    public void removeTests(){
        Set<String> col1 = new HashSet<>(Arrays.asList("1","3","5"));
        Set<String> col2 = new HashSet<>(Arrays.asList("1","2","3","4","5"));
        Set<String> col3 = new HashSet<>(Arrays.asList("1","7","8","9"));
        log.info("col1 remove result = {},col1={}",col1.removeAll(col2), col1);
        log.info("col3 remove result = {},col3={}",col3.removeAll(col2), col3);
        log.info("col2={}",col2);
    }


    @Test
    public void optionalTest() {
        List<Student> test = stus.stream().filter(val->val.getName().equals("大小")).collect(Collectors.toList());
        List<String> names = Optional.ofNullable(test).map(l->l.stream().map(Student::getName).collect(Collectors.toList())).orElse(Collections.emptyList());
        log.debug("names={}",names);
        Student daxia = Optional.ofNullable(test).orElse(new ArrayList<>()).stream().
                filter(val->val.getBirthday()!=null).findAny().orElse(null);
        log.debug("daxia1 = {}",daxia);

        daxia = Optional.ofNullable(test).map(t-> {return t.stream().
                filter(val->val.getBirthday()!=null).findAny().orElse(null);}).orElse(null);
        log.debug("daxia2 = {}",daxia);
    }
}
