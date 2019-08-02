package com.fr.swift.source.alloter.impl.time.function;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * @author Marvin
 * @date 7/23/2019
 * @description
 * @since swift 1.1
 */
public class TimePartitionsFunctionTest {

    @Test
    public void testDefaultTimePartitionsFunction() {
        TimePartitionsFunction function1 = new TimePartitionsFunction(TimePartitionsType.YEAR);
        TimePartitionsFunction function2 = new TimePartitionsFunction(TimePartitionsType.QUARTER);
        TimePartitionsFunction function3 = new TimePartitionsFunction(TimePartitionsType.MONTH);
        for (int i = 1; i <= 1000000; i++) {
            Random random = new Random();
            long time = 1563849076000L - random.nextLong();
            int index1 = function1.indexOf(time);
            int index2 = function2.indexOf(time);
            int index3 = function3.indexOf(time);
            Assert.assertTrue(index1 >= 1970);
            Assert.assertTrue(index1 <= 2019);
            Assert.assertTrue(index2 >= 0);
            Assert.assertTrue(index2 <= 3);
            Assert.assertTrue(index3 >= 0);
            Assert.assertTrue(index3 <= 11);
        }
    }
}