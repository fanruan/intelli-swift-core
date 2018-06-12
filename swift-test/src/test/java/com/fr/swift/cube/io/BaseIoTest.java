package com.fr.swift.cube.io;


import com.fr.swift.test.Preparer;
import com.fr.swift.test.TestResource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/6
 */
public abstract class BaseIoTest {
    final Random r = new Random();
    static final int BOUND = 100000;
    public static final String CUBES_PATH = TestResource.getRunPath(BaseIoTest.class) + "/cubes/table/seg0/column";

    @BeforeClass
    public static void boot() throws Exception {
        Preparer.prepareCubeBuild();
    }

    @Test
    public abstract void testOverwritePutThenGet();

    @Test
    public abstract void testPutThenGet();

    @Test
    public abstract void testMemPutThenGet();
}
