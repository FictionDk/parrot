package com.fictio.parrot.demo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoubleTests {
    private List<Student> stus;

    private long denominator;

    private long numerator;

    private LocalDate processDate;

    private LocalDate reportDate;

    @Before
    public void preTest() {
        stus = new ArrayList<>();
        stus.add(new Student(1,"张三",Student.Sex.MALE,LocalDate.of(2000, 1, 1)));
        stus.add(new Student(2,"李四",Student.Sex.FEMALE,LocalDate.of(1990, 1, 1)));
        stus.add(new Student(3,"李四",Student.Sex.MALE,LocalDate.of(1993, 1, 1)));
        stus.add(new Student(4,"王五",Student.Sex.MALE,LocalDate.of(1991, 1, 1)));
        stus.add(new Student(5,"金唯",Student.Sex.FEMALE,LocalDate.of(1992, 1, 1)));
        stus.add(new Student(6,"金唯",Student.Sex.FEMALE,null));

        processDate = LocalDate.now();
        reportDate = processDate.plusDays(-10);
    }


    @Test
    public void test() {
        denominator = stus.stream().filter(u->u.getBirthday() != null).filter(u->u.getBirthday().isAfter(LocalDate.now())).count();
        numerator = 0L;
        log.debug("denominator != 0 is {}",denominator != 0);
        print();

        numerator = 1L;
        print();

        numerator = -1L;
        print();


        double factor = 1.0;
        double day1 = processDate.toEpochDay() * factor;
        double day2 = reportDate.toEpochDay() * factor;

        log.debug("{} / {} = {}",day2,day1,day2/day1);

        factor = 18300;
        day1 = day1 - factor;
        day2 = day2 - factor;
        log.debug("{} / {} = {}",day2,day1,day2/day1);

        reportDate = processDate;
        getTimelinessPrint();
        reportDate = processDate.plusDays(1);
        getTimelinessPrint();
        reportDate = processDate.plusDays(-1);
        getTimelinessPrint();
        reportDate = processDate.plusDays(-10);
        getTimelinessPrint();

    }

    public void print() {
        log.debug("{}/{} = {}",numerator,denominator,numerator*1.0/denominator*1.0);
    }

    private void getTimelinessPrint() {
        log.debug("reportDate:{},processDate: {} -> {}",
                toString(reportDate),toString(processDate),getTimeliness(processDate,reportDate));
    }

    @After
    public void after() {
        log.debug("Finished!");
    }

    /**
     * <p> 计算获取及时性 结果数据
     *
     * @param processData
     * @param reportDate
     * @return
     */
    private double getTimeliness(LocalDate processData,LocalDate reportDate) {
        if(processData == null || reportDate == null) return 0.0;
        long gaps = processData.toEpochDay() - reportDate.toEpochDay();
        if(gaps < 0) return 0.0;
        else if(gaps <= 1) return 1.0;
        else return 1 / Math.log((double) gaps);
    }

    private String toString(LocalDate date) {
        if(date == null) return "None date";
        else return date.format(DateTimeFormatter.ISO_DATE);
    }

    @Test
    public void mathLogTest() {
        for(int i = 0; i <100; i++) {
            log.debug("Math.log({}) = {}",i,Math.log((double) i));
            log.debug("1/Math.log({}) = {}",i,1/Math.log((double) i));
        }
    }

}
