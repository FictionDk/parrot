package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalDateTimeDemo {

    @Test
    public void test() {
        LocalDateTime c = LocalDateTime.of(2019, 7, 20, 0, 0);
        long gapDays = LocalDateTime.now().until(c, ChronoUnit.DAYS);
        log.debug("--> {}",gapDays);
        gapDays = LocalDate.now().until(c.toLocalDate(), ChronoUnit.DAYS);
        log.debug("--> {}",Math.abs(gapDays));
        
    }
}
