package com.fictio.parrot.logic.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

enum Size{
    SMALL,MEDIUM,BIG
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
    private String name;
    private int age;
    private boolean isMan;
    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + ", isMan=" + isMan + "]";
    }
}

@EqualsAndHashCode(callSuper=true)
@Data
class Coder extends Person{
    private Size power;
    private String[] language;
}

@Slf4j
public class Testing {
    
    /**
     * <li> 数组类型的Class对象
     */
    @Test
    public void arrayClassTest() {
        String [] strArr = new String[10];
        int [][] twoDimArr = new int[3][2];
        int [] oneDimArr = new int[10];
        Class<? extends String[]> strArrCls = strArr.getClass();
        Class<? extends int [][]> twoDimArrCls = twoDimArr.getClass();
        Class<? extends int []> oneDimArrCls = oneDimArr.getClass();
        
        log.info("strArrCls: {}",strArrCls.toGenericString());
        log.info("twoDimArrCls: {}",twoDimArrCls.toGenericString());
        log.info("oneDimArrCls: {}",oneDimArrCls.toGenericString());
    }
    
    /**
     * <li> 枚举类型的Class对象
     */
    @Test
    public void eumsClassTest() {
        Class<? extends Size> enumCls = Size.MEDIUM.getClass();
        log.info("enumCls: {}",enumCls.toGenericString());
    }
    
    /**
     * <li> class.forName()使用
     */
    @Test
    public void forNameTest() {
        try {
            Class<?> cls = Class.forName("java.util.HashMap");
            log.info("cls: {}",cls.toGenericString());
            @SuppressWarnings("unchecked")
            Map<String,Object> mapper = (Map<String, Object>) cls.newInstance();
            mapper.put("key", "value");
            log.info("map: {}",mapper);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("new Instants error");
            e.printStackTrace();
        }
    }
    
    /**
     * <li> 使用 Field.set(obj,value)方法,不需要字段是否有set方法,可以直接赋值到变量
     * <li> 如果是静态变量,obj可以为null
     * <li> 可以使用Field.getModifiers()获取int,使用Modifier中的静态方法查看字段修饰符
     */
    @Test
    public void fieldTest() {
        Person p = new Person();
        Class<? extends Person> cls = p.getClass();
        Field[] fileds = cls.getDeclaredFields();
        for(Field f : fileds) {
            log.info("f:{}, p:{}",f,p.toString());
            f.setAccessible(true);
            // Field.getName(), Field.getType()
            if("name".equals(f.getName()) && String.class.equals(f.getType()))
                try {
                    f.set(p, "小王");
                    log.info("set filed sucessfully! ");
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            int mod = f.getModifiers();
            log.info("{}",Modifier.toString(mod));
            log.info("isPublic:{}",Modifier.isPublic(mod));
            log.info("isStatic:{}",Modifier.isStatic(mod));
            log.info("isVolatile:{}",Modifier.isVolatile(mod));
        }
    }
    
    @Test
    public void methodTest() {
        Class<? extends Coder> cls = Coder.class;
        Method[] methods = cls.getMethods();
        Coder coder = null;
        try {
            coder = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(coder == null) return;
        for( Method method : methods) {
            log.info("Method: {}, Coder: {}",method.getName(),coder);
            if("setPower".equals(method.getName()))
                try {
                    method.invoke(coder, Size.BIG);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("{} Method.invoke({},{}) failed",method.getName(),coder,Size.BIG);
                    e.printStackTrace();
                }
        }
    }
    
    @Test
    public void staticMethodTest() {
        Class<?> cls = Integer.class;
        Method method = null;
        try {
            method = cls.getMethod("parseInt", new Class[] {String.class});
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Class.getMethod({},{}),Failed","parseInt",new Class[] {String.class});
            e.printStackTrace();
        }
        if(method == null) return;
        try {
            log.info("static mehotd invoke success, result is: {}",method.invoke(null, "123"));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("{} Method.invoke({},{}) failed",method.getName(),null,"123");
            e.printStackTrace();
        }
    }
    
    @Test
    public void constructMethodTest() {
        Class<Person> cls = Person.class;
        Constructor<Person> csts = null;
        try {
            csts = cls.getConstructor(new Class[] {String.class,int.class,boolean.class});
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        if(csts == null) return;
        try {
            Person p = csts.newInstance("小王",20,true);
            log.info("Constructor new instance success, {}",p);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            log.error("{} Constructor.newInstance({},{},{}) failed",csts,"小王",20,true);
        }
        
    }
}
