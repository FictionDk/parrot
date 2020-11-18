package com.fictio.parrot.demo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CollectionSize {

    /**
     * <p> 通过反射查看容器的扩容情况
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public void mapSizeTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Map<String, String> sizeTestMap = new HashMap<>(3);
        Class<?> mapClazz = sizeTestMap.getClass();
        Field thresholdField = mapClazz.getDeclaredField("threshold");
        thresholdField.setAccessible(true);
        for(int i = 0; i < 100 ; i ++) {
            sizeTestMap.put(String.valueOf(i), "Go"+i);
            System.out.print("threshold: "+ thresholdField.get(sizeTestMap));
            System.out.print("; cap: " + ((int) thresholdField.get(sizeTestMap)/0.75));
            System.out.println("; size: " + sizeTestMap.size());
        }
    }

    @Test
    public void removeTests() {
        Map<String, Object> demo = new HashMap<>();
        demo.put("aaa", "1111");
        demo.put("bbb", "2222");
        System.out.println(demo.remove("aaa"));
        System.out.println(demo);
        System.out.println(demo.remove("b"));
    }

}
