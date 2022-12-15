package com.fictio.parrot.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class LocalTests {
    @SuppressWarnings("unused")
    final String memberStr = "Hello2";  // final修饰的成员变量初始化时必须有值

    /**
     * final关键字测试,局部变量
     */
    @Test
    @SuppressWarnings("all")
    public void finalTest(){
        String a = "hello2", c = "hello";
        final String b = "hello";  // 编译器当成常量处理
        String d = b + 2, e = c + 2, f = new StringBuilder("hello").append(2).toString();
        String g = new String("hello2");
        log.info("{}=={},{}",getCode(a),getCode(d), a == d); // 常量池,地址相同,b+2在编译器有优化处理
        log.info("{}=={},{}",getCode(a),getCode(e), a == e); // 一个常量池,一个堆内存
        log.info("{}=={},{}",getCode(a),getCode(f), a == f);
        log.info("{}=={},{}",getCode(a),getCode(g), a == g);
        // memberStr = "h"; 成员变量初始化后不能变更内容
        String m = "hello";
        final String n = m;
        m += 2;
        log.info("m = {}, n = {}", m, n);

        int i = 10;
        final int j = i;
        i++;
        log.info("i = {}, j = {}", i, j);
    }
    private int getCode(Object obj){
        return System.identityHashCode(obj);
    }
}
