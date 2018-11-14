package com.fr.swift.segment.column.impl.base;

import org.junit.Rule;
import org.junit.rules.TestRule;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/10
 */
public abstract class BaseDetailColumnTest {
    static final String BASE_PATH = "cubes/table/seg0/column";
    Random r = new Random();
    int size = 1000000;

    @Rule
    public TestRule getExternalResource() throws Exception {
        return (TestRule) Class.forName("com.fr.swift.test.external.BuildCubeResource").newInstance();
    }
}