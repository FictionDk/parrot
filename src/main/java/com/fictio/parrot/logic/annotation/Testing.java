package com.fictio.parrot.logic.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

@Target({ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Inject {
    boolean optional() default false;
}

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@interface InheritDemo{
}

@InheritDemo
class Base {
}

class SimpleFormatter {
    public static String format(Object obj) {
        Class<?> cls = obj.getClass();
        StringBuilder sb = new StringBuilder();
        for(Field f : cls.getDeclaredFields()) {
            if(!f.isAccessible()) f.setAccessible(true);
            Label label = f.getAnnotation(Label.class);
            String name = label != null ? label.value() : f.getName();
            Object val = null;
            try {
                val = f.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if(val != null && f.getType() == Date.class)
                val = formatDate(f,val);
            sb.append(name + ":" + val +"\n");
        }
        return sb.toString();
    }

    private static Object formatDate(Field f, Object val) {
        Format format = f.getAnnotation(Format.class);
        if(format != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format.pattern());
            sdf.setTimeZone(TimeZone.getTimeZone(format.timezone()));
            return sdf.format(val);
        }
        return val;
    }
}

public class Testing {

    static class Child extends Base {
    }
    
    static class Student {
        @Label("姓名")
        String name;
        @Label("出生日期")
        @Format(pattern="yyyy/MM/dd")
        Date born;
        @Label("分数")
        Double score;
        public Student(String name, Date born, Double score) {
            super();
            this.name = name;
            this.born = born;
            this.score = score;
        }
    }
    
    @Test
    public void inheritedDemo() {
        // Child没有显示声明拥有InheritDemo注解,但通过继承Base拥有InheritDemo注解
        // 如果将InheritDemo注解上的@Inherited去除,则注解不会被继承
        System.out.println(Child.class.isAnnotationPresent(InheritDemo.class));
    }
    
    /**
     * 使用注解格式化对象
     * 
     * @throws ParseException
     */
    @Test
    public void formatDemo() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Student zhangsan = new Student("张三",sdf.parse("1990-12-12"),80.0);
        System.out.println(SimpleFormatter.format(zhangsan));
    }
    
    static class SerivceB {
        void action() {
            System.out.println("get B start !");
        }
    }
    
    static class ServiceA {
        @SimpleInject
        SerivceB b;
        public void callB() {
            b.action();
        }
    }

    /**
     * 使用SimpleContainer获取ServiceA对象,同时注入SerivceB对象
     */
    @Test
    public void diDemo() {
        ServiceA a = SimpleContainer.getInstance(ServiceA.class);
        a.callB();
    }
    
}
