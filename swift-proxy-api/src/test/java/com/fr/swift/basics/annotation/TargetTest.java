package com.fr.swift.basics.annotation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/11/1
 */
public class TargetTest {

    @Test
    public void testTarget() {
        assertEquals(Target.NONE.name(), "NONE");
        assertEquals(Target.HISTORY.name(), "HISTORY");
        assertEquals(Target.ANALYSE.name(), "ANALYSE");
        assertEquals(Target.REAL_TIME.name(), "REAL_TIME");
        assertEquals(Target.INDEXING.name(), "INDEXING");
        assertEquals(Target.ALL.name(), "ALL");

    }
}