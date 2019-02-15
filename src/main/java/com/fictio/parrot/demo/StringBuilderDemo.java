package com.fictio.parrot.demo;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class StringBuilderDemo {

    public String setToString(Set<String> ids) {
        if(ids == null || ids.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder("(");
        ids.forEach(id->{
            sb.append(id);
            sb.append(",");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }
    
    @Test
    public void test() {
        
        Set<String> demo = new HashSet<>();
        demo.add("11014");
        demo.add("11015");
        demo.add("11016");
        
        String r = setToString(demo);
        System.out.println(r);
        
    }
    
}
