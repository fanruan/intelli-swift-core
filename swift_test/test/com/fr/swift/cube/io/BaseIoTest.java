package com.fr.swift.cube.io;

import junit.framework.TestCase;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/6
 */
public abstract class BaseIoTest extends TestCase {
    final Random r = new Random();
    static final int BOUND = 100000;
    public static final String CUBES_PATH = System.getProperty("user.dir") + "/cubes/";

    public abstract void testOverwritePutThenGet();

    public abstract void testPutThenGet();

    public abstract void testMemPutThenGet();
}
