package com.fictio.parrot.demo;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassEqualDemo {
    
    private static class Student {
        Long id;
        String name;
        public Student(Long id,String name) {
            this.id = id;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return "Student [id=" + id + ", name=" + name + "]";
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Student other = (Student) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }
    }
    
    @Test
    public void demo() {
        
        Student s1 = new Student(1L,"张三");
        Student s2 = new Student(1L, "李四");
        Student s3 = new Student(2L, "王五");
        
        log.info("s1.equals(s2):{}",s1.equals(s2));
        log.info("s1.equals(s3):{}",s1.equals(s3));
        log.info("s2.equals(s3):{}",s2.equals(s3));
        
        Set<Student> sets = new HashSet<>();
        sets.add(s1);
        sets.add(s2);
        sets.add(s3);
        log.info("Sets: {}",sets);
        
        
    }

}
