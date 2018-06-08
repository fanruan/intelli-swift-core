package com.fr.swift.cube.io;


import org.junit.Test;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/6
 */
public abstract class BaseIoTest {
    final Random r = new Random();
    static final int BOUND = 100000;
    public static final String CUBES_PATH = System.getProperty("user.dir") + "/cubes/table/seg0/column";

    @Test
    public abstract void testOverwritePutThenGet();

    @Test
    public abstract void testPutThenGet();

    @Test
    public abstract void testMemPutThenGet();
}
