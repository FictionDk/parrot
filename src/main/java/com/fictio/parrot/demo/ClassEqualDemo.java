package com.fictio.parrot.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public void boxingTest() {
        int i = 0;
        Integer j = new Integer(0);
        System.out.println(i == j);
        System.out.println(j.equals(i));
    }

    static class A2{ 
        public static void main(String[] args){
            int[] a={2,4,6,8,3,6,9,12};
            doSomething(a,0,a.length-1);
            for(int i=0;i<=a.length-1;i++)
            System.out.print(a[i]+" ");
        } 
        private static void doSomething(int[] a,int start,int end){
            if(start<end){
                int p=core(a,start,end);
                doSomething(a,start,p-1);
                doSomething(a,p+1,end);
            }
        }
        private static int core(int[] a,int start,int end){
            int x=a[end];
            int i=start;
            for(int j=start;j<=end-1;j++){
                if(a[j]>=x){
                    swap(a,i,j);
                    i++; 
                }
        }    
        swap(a,i,end); 
            return i;
        } 
        private static void swap(int[] a,int i,int j) {
            int tmp=a[i];
            a[i]=a[j];
            a[j]=tmp;
        }
   } 

   static class Base{
        private String baseName = "base";
        public Base(){
            callName();
        }
        public void callName(){
            System.out.println(baseName);
        }
        static class Sub extends Base{
            private String baseName = "sub";
            public void callName(){
                System.out.println(baseName) ;
            }
        }
        public static void main(String[] args){
            @SuppressWarnings("unused")
            Base b = new Sub();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String args[]) {
       List listA = new ArrayList();
       listA.add(0);
       List listB = listA;
       System.out.println(listA.get(0) instanceof Integer);
       System.out.println(listB.get(0) instanceof Integer);
    }

    static class Example{
        String str=new String("hello");
        char[]ch={'a','b'};
        public static void main(String args[]){
            Example ex=new Example();
            ex.change(ex.str,ex.ch);
            System.out.print(ex.str+" and ");
            System.out.print(ex.ch);
        }
        public void change(String str,char ch[]){
            str="test ok";
            ch[0]='c';
        }
    }

    static class Car extends Vehicle{
        public static void main (String[] args){
            new  Car().run();
        }
        private final void run(){
            System. out. println ("Car");
        }
    }
    static class Vehicle{
        @SuppressWarnings("unused")
        private final void run(){
            System. out. println("Vehicle");
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
