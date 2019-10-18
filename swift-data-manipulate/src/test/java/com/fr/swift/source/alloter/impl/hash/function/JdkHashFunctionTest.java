package com.fr.swift.source.alloter.impl.hash.function;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class JdkHashFunctionTest {

    @Test
    public void testJdkHashFunction() {
        HashFunction function = new JdkHashFunction(8);
        Assert.assertEquals(function.getType(), HashType.JDK);
        for (int i = 0; i < 1000; i++) {
            String uuid = UUID.randomUUID().toString();
            int index = function.indexOf(uuid);
            Assert.assertTrue(index >= 0);
            Assert.assertTrue(index <= 7);

        }
    }
}
