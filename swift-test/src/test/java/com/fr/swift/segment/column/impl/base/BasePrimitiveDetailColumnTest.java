package com.fr.swift.segment.column.impl.base;

import com.fr.swift.test.TestIo;
import org.junit.Test;

import java.util.Random;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;

/**
 * @author anchore
 * @date 2017/11/10
 */
public abstract class BasePrimitiveDetailColumnTest extends TestIo {
    static final String BASE_PATH = CUBES_PATH;
    Random r = new Random();
    int size = 1000000;

    @Test
    public abstract void testPutThenGet();

}