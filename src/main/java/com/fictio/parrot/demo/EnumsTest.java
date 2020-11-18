package com.fictio.parrot.demo;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

enum RhBloodType{
    NEGATIVE("1"),
    POSITIVE("2");
    private String value;
    private RhBloodType(String val) {
        this.value = val;
    }
    public String toString() {
        return this.value;
    }
    public static RhBloodType getRhBloodType(String val) {
        switch (val) {
        case "1":
            return NEGATIVE;
        case "2":
            return POSITIVE;
        default:
            return null;
        }
    }
}

enum Level {
    HIGH, LOW;
}

@Slf4j
public class EnumsTest {

    @Test
    public void test() {
        RhBloodType type = RhBloodType.getRhBloodType("1");
        log.info("Type: {}",type);
        log.info("{}", Level.valueOf("HIGH"));
        log.info("{}", Level.valueOf("HIGH1"));
    }

    @Test
    public void doubleFromString() {
        try {
            double i = Double.parseDouble(null);
            log.info("i={}",i);
        }catch(Exception e) {
            log.error("e={}",e.toString());
        }
    }

}
