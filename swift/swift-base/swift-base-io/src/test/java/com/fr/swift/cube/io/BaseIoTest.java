package com.fr.swift.cube.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/6
 */
public abstract class BaseIoTest {
    final Random r = new Random();
    static final int BOUND = 100000;
    public static final String CUBES_PATH = "cubes/table/seg0/column";

    long pos = 0;

    @Rule
    public TestRule getExternalResource() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }

    @Test
    public abstract void testOverwritePutThenGet();

    @Test
    public abstract void testPutThenGet();

    @Test
    public abstract void testMemPutThenGet();
}
