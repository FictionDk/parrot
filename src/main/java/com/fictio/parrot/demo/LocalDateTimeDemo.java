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

    @Test
    public void compareTest() {
        LocalDate start = LocalDate.now().plusDays(-9);
        LocalDate end = LocalDate.now();

        int i = 0;
        while(start.isBefore(end)) {
            log.debug("{} isbefore {} = {}",start,end,start.isBefore(end));
            start = start.plusDays(1);
            i ++;
            if(i > 20) break;
        }
    }
}
