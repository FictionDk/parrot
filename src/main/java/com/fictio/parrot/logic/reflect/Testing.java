package com.fictio.parrot.logic.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * <li> 基本类型不支持forName()的使用
     * <li> Java9: forName(Module module, String name)
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
    
    @Test
    public void classCheckTest() {
        //Arrays.asList()得到的是一个特殊的ArrayList,java.util.Arrays.ArrayList
        List<String> list = Arrays.asList("1");
        log.info("Arrays.asList is {}",list.getClass().getCanonicalName());
        //new ArrayList<>()是普通的,java.util.ArrayList
        list = new ArrayList<>();
        log.info("Arrays.asList is {}",list.getClass().getCanonicalName());
        if(list instanceof ArrayList) log.info("list instanceof ArrayList is right!");
        Class<?> cls = null;
        try {
            cls = Class.forName("java.util.ArrayList");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(cls == null) return;
        if(cls.isInstance(list)) log.info("Class<ArrayList>.isInstance is list is right!");
        
        Coder coder = new Coder();
        coder.setName("王二");
        Person p = toType(coder, Person.class);
        log.info("{} Dynamic cast to {} !",coder,p);
    }
    
    @Test
    public void classJudgeTest() {
        List<String> list = Arrays.asList("1");
        Class<?> cls = list.getClass();
        //是否是数组
        log.info("cls is Array {}",cls.isArray());
        //是否是基本类型
        log.info("cls is Primitive {}",cls.isPrimitive());
        //是否是接口
        log.info("cls is Interface {}",cls.isInterface());
        //是否是枚举
        log.info("cls is Enum {}",cls.isEnum());
        //是否是注解
        log.info("cls is Annotation {}",cls.isAnnotation());
        //是否是匿名内部类
        log.info("cls is AnonymousClass {}",cls.isAnonymousClass());
        //是否是成员类;
        log.info("cls is MemberClass {}",cls.isMemberClass());
        //是否是本地类;本地类定义在方法内,不是匿名类
        log.info("cls is LocalClass {}",cls.isLocalClass());
    }
    
    /**
     * java.lang.reflect.Array
     * <li> 数组的反射
     * 
     */
    @Test
    public void arrayTest() {
        String[] arr = new String[] {};
        log.info("String[] componentType: {}",arr.getClass().getComponentType());
        
        arr = (String[]) Array.newInstance(String.class, 10);
        arr[0] = "test";
        log.info("Arr[0] = {}",arr[0]);
        
        String[][] twoDimArr = (String[][]) Array.newInstance(String.class, 2, 10);
        twoDimArr[0][0] = "test";
        log.info("Arr[0][0] = {}",twoDimArr[0][0]);
        Array.set(arr, 1, "java.lang.reflect.Array.set()");
        log.info("Array.get(1) = {}",(String) Array.get(arr, 1));
        log.info("Array.getLength = {}",Array.getLength(arr));
    }
    
    /**
     * <li> 枚举
     */
    @Test
    public void enumTest() {
        Size[] es = Size.class.getEnumConstants();
        log.info("Enum.Class.getEnumConstants: {}",
            Stream.of(es).map(e->e.toString()).collect(Collectors.toList()));
    }
    
    private <T> T toType(Object obj, Class<T> cls) { 
        return cls.cast(obj);
    }
}
