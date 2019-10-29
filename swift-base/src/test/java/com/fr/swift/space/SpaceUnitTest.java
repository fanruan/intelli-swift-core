package com.fr.swift.space;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/3/20
 */
public class SpaceUnitTest {

    @Test
    public void ofBytes() {
        assertEquals(1024 / (double) (1L << 0), SpaceUnit.B.ofBytes(1024), 0);
        assertEquals(1024 / (double) (1L << 10), SpaceUnit.KB.ofBytes(1024), 0);
        assertEquals(1024 / (double) (1L << 20), SpaceUnit.MB.ofBytes(1024), 0);
        assertEquals(1024 / (double) (1L << 30), SpaceUnit.GB.ofBytes(1024), 0);
        assertEquals(1024 / (double) (1L << 40), SpaceUnit.TB.ofBytes(1024), 0);
    }

    @Test
    public void toBytes() {
        assertEquals(1024 * (1L << 0), SpaceUnit.B.toBytes(1024), 0);
        assertEquals(1024 * (1L << 10), SpaceUnit.KB.toBytes(1024), 0);
        assertEquals(1024 * (1L << 20), SpaceUnit.MB.toBytes(1024), 0);
        assertEquals(1024 * (1L << 30), SpaceUnit.GB.toBytes(1024), 0);
        assertEquals(1024 * (1L << 40), SpaceUnit.TB.toBytes(1024), 0);
    }
}