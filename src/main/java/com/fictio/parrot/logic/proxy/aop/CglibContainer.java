package com.fictio.parrot.logic.proxy.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CglibContainer {
    
    public static enum InterceptPoint {
        BEFORE, AFTER, EXCEPTION
    }
    
    static Class<?>[] aspects = new Class<?>[] {ServiceLogAspect.class};
    
    static Map<Class<?>,Map<InterceptPoint,List<Method>>> interceptMethodsMap = new ConcurrentHashMap<>();
    
    static {
        init();
    }
    
    private static void init() {
        for(Class<?> cls : aspects) {
            Aspect aspect = cls.getAnnotation(Aspect.class);
            if(aspect != null) {
                Method before = getMethod(cls,"before",new Class<?>[] {Object.class,Method.class,Object[].class});
                Method after = getMethod(cls,"after",new Class<?>[] {Object.class,Method.class,Object[].class});
                
                Class<?>[] intercepttedArr = aspect.value();
                for(Class<?> interceptted : intercepttedArr) {
                    addInterceptMethod(interceptted,InterceptPoint.BEFORE,before);
                    addInterceptMethod(interceptted,InterceptPoint.AFTER,after);
                }
            }
        }
    }

    private static void addInterceptMethod(Class<?> cls, InterceptPoint point, Method method) {
        if(method == null) return;
        Map<InterceptPoint,List<Method>> map = interceptMethodsMap.get(cls);
        if(map == null) {
            map = new HashMap<>();
            interceptMethodsMap.put(cls, map);
        }
        List<Method> methods = map.get(point);
        if(methods == null) {
            methods = new ArrayList<>();
            map.put(point, methods);
        }
        methods.add(method);
    }

    private static Method getMethod(Class<?> cls, String name, Class<?>[] parameterTypes) {
        try {
            return cls.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

}
