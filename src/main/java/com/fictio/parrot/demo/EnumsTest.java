package com.fictio.parrot.demo;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumsTest {
    enum RhBloodType{
        NEGATIVE("1"),
        POSITIVE("2");
        private final String value;
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
    @Test
    public void test() {
        RhBloodType type = RhBloodType.getRhBloodType("1");
        log.info("Type: {}",type);
        log.info("{}", Level.valueOf("HIGH"));
        log.info("{}", Level.valueOf("HIGH1"));
    }
}
