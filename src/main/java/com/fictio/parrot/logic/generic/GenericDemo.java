package com.fictio.parrot.logic.generic;

import java.util.Random;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericDemo {

    @Test
    public void dynamicTests() {
        DynamicArray<Double> doubleList = new DynamicArrayImpl<>();
        Random rnd = new Random();
        for(int i = 0; i < 100; i++) doubleList.add(rnd.nextDouble());
        int rndIndex = rnd.nextInt(doubleList.size());
        log.debug("Arr[{}] = {} ", rndIndex, doubleList.get(rndIndex));
    }
}
