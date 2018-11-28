package com.fr.swift.compare;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class ComparatorsTest {

    @Test
    public void numberAsc() {
        double d1 = Double.MAX_VALUE, d2 = Double.MIN_VALUE;
        long l1 = Long.MAX_VALUE, l2 = Long.MIN_VALUE;
        int i1 = Integer.MAX_VALUE, i2 = Integer.MIN_VALUE;
        short s1 = Short.MAX_VALUE, s2 = Short.MIN_VALUE;
        byte b1 = Byte.MAX_VALUE, b2 = Byte.MIN_VALUE;

        Comparator<Number> c = Comparators.numberAsc();

        assertEquals(0, c.compare(d1, d1));
        assertEquals(0, c.compare(l1, l1));
        assertEquals(0, c.compare(i1, i1));

        assertEquals(1, c.compare(d1, d2));
        assertEquals(1, c.compare(l1, l2));
        assertEquals(1, c.compare(i1, i2));

        assertEquals(-1, c.compare(d2, d1));
        assertEquals(-1, c.compare(l2, l1));
        assertEquals(-1, c.compare(i2, i1));

        assertEquals(1, c.compare(d1, l1));
        assertEquals(1, c.compare(d1, i1));
        assertEquals(1, c.compare(l1, i1));

        assertEquals(-1, c.compare(l1, d1));
        assertEquals(-1, c.compare(i1, d1));
        assertEquals(-1, c.compare(i1, l1));

        assertEquals(1, c.compare(s1, i2));
        assertEquals(1, c.compare(b1, l2));
        assertEquals(-1, c.compare(s1, d1));
        assertEquals(-1, c.compare(b1, l1));
        assertEquals(-1, c.compare(s2, i1));
        assertEquals(-1, c.compare(b2, l1));

        assertEquals(0, c.compare(null, null));
        assertEquals(1, c.compare(d1, null));
        assertEquals(-1, c.compare(null, l1));
    }
}