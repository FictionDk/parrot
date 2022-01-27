package com.fictio.parrot.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public void intToStrTest(){
        Float f = 200f;
        String str = String.valueOf(f);
        System.out.println(str);
    }

    @Test
    public void strTest(){
        List<String> bags = Arrays.asList("020242152061345D6437000","020242152060345D6437000","020242152069545D6437000");
        bags = bags.stream().map(s->s = String.format("%s00%s", s.substring(0, 13), s.substring(15, 23))).collect(Collectors.toList());
        for (String bag : bags) // 使用for(...)无法修改容器内字符引用
            log.debug("-> {}",bag);
    }

    @Test
    public void test() {

        Set<String> demo = new HashSet<>();
        demo.add("11014");
        demo.add("11015");
        demo.add("11016");

        String r = setToString(demo);
        System.out.println(r);

        String uid = "u.24";
        System.out.println(uid.startsWith("u."));
        log.debug("{}",Long.parseLong(uid.replace("u.", "")));

        System.out.println(3%3);

        System.out.println(2=="test;tt".split(";").length);
        LocalDateTime createTime = LocalDateTime.parse("2021-12-07T15:46:07.325", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime expTime = createTime.plusMinutes(120);
        Boolean isExp = expTime.isAfter(LocalDateTime.now());
        log.debug("{} is after {} = {}",expTime,LocalDateTime.now(),isExp);
    }

}
