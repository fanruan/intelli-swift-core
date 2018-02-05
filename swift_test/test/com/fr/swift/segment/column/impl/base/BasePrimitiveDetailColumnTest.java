package com.fr.swift.segment.column.impl.base;

import junit.framework.TestCase;

import java.util.Random;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;

/**
 * @author anchore
 * @date 2017/11/10
 */
public abstract class BasePrimitiveDetailColumnTest extends TestCase {
    static final String BASE_PATH = CUBES_PATH;
    Random r = new Random();
    int size = 1000000;

    public abstract void testPutThenGet();

}