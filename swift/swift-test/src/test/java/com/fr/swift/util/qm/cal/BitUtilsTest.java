package com.fr.swift.util.qm.cal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Lyon on 2018/4/21.
 */
public class BitUtilsTest {

    @Test
    public void binLength() {
        assertTrue(BitUtils.binLength(10) == 4);
        assertTrue(BitUtils.binLength(0) == 1);
        assertTrue(BitUtils.binLength(2) == 2);
        assertTrue(BitUtils.binLength(8) == 4);
    }

    @Test
    public void countOf1InBinary() {
        assertEquals(BitUtils.countOf1InBinary(4), 1);
        assertEquals(BitUtils.countOf1InBinary(5), 2);
        assertEquals(BitUtils.countOf1InBinary(13), 3);
    }
}