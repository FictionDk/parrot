package com.fictio.parrot.logic.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

class SimpleMapper {
    
    public static String toString(Object obj) {
        Class<?> cls = obj.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getName()+"\n");
        
        try {
            for(Field f : cls.getDeclaredFields()) {
                if(!f.isAccessible()) f.setAccessible(true);
                    sb.append(f.getName()+"="+f.get(obj).toString()+"\n");
                } 
            }
        catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("get field failed,"+e);
        }
        
        return sb.toString();
    }
    
    public static Object fromString(String str) {
        String[] lines = str.split("\n");
        if(lines.length<1) throw new IllegalAccessError(str);
        
        Class<?> cls = null;
        try {
            cls = Class.forName(lines[0]);
            Object obj = cls.newInstance();
            if(lines.length > 1) {
                for(int i = 1; i < lines.length; i++) {
                    String[] fv = lines[i].split("=");
                    if(fv.length != 2) throw new IllegalArgumentException(lines[i]);
                    Field f = cls.getDeclaredField(fv[0]);
                    if(!f.isAccessible()) f.setAccessible(true);
                    setFieldValue(f,obj,fv[1]);
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } 
    }
    
    /**
     * <li> 为指定字段添加值
     * 
     * @param f
     * @param obj
     * @param value
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws InstantiationException 
     */
    private static void setFieldValue(Field f, Object obj, String value) 
            throws NumberFormatException, IllegalArgumentException, IllegalAccessException, 
            NoSuchMethodException, SecurityException, 
            InstantiationException, InvocationTargetException {
        Class<?> type = f.getType();
        if(type == int.class) f.setInt(obj, Integer.parseInt(value));
        else if(type == byte.class) f.setByte(obj, Byte.parseByte(value));
        else if(type == boolean.class) f.setBoolean(obj, Boolean.parseBoolean(value));
        else if(type == short.class) f.setShort(obj, Short.parseShort(value));
        else if(type == long.class) f.setLong(obj, Long.parseLong(value));
        else if(type == float.class) f.setFloat(obj, Float.parseFloat(value));
        else if(type == double.class) f.setDouble(obj, Double.parseDouble(value));
        else if(type == char.class) f.setChar(obj, value.charAt(0));
        else if(type == String.class) f.set(obj,value);
        else {
            Constructor<?> ctor = type.getConstructor(new Class[] {String.class});
            f.set(obj, ctor.newInstance(value));
        }
        
        
    }
}

/**
 * <li> 利用反射实现简单序列号/反序列化类;
 *
 */
@Slf4j
public class SimpleMapperDemo {
    
    private Person coder;
    
    @Before
    public void preTest() {
        coder = new Person();
        coder.setAge(20);
        coder.setName("Tomcat");
        coder.setMan(true);
    }
    
    @Test
    public void test() {
        log.info("init coder {} ",coder);
        String str = SimpleMapper.toString(coder);
        log.info("coder str is: {} ",str);
        Person coder = (Person) SimpleMapper.fromString(str);
        log.info("new coder {} ",coder);
    }

}
