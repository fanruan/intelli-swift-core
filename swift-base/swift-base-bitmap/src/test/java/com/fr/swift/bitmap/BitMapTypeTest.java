package com.fr.swift.bitmap;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2019/3/26
 */
public class BitMapTypeTest {

    @Test
    public void getHead() {
        assertArrayEquals(
                new BitMapType[]{
                        BitMapType.ROARING_IMMUTABLE, BitMapType.ROARING_MUTABLE,
                        BitMapType.BIT_SET_IMMUTABLE, BitMapType.BIT_SET_MUTABLE,
                        BitMapType.ALL_SHOW, BitMapType.ID, BitMapType.RANGE, BitMapType.EMPTY}, BitMapType.values());

        BitMapType[] values = BitMapType.values();
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, values[i].getHead());
        }
    }

    @Test
    public void ofHead() {
        BitMapType[] types = BitMapType.values();
        for (int i = 0; i < types.length; i++) {
            assertEquals(types[i], BitMapType.ofHead((byte) i));
        }
        try {
            BitMapType.ofHead((byte) -1);
            fail();
        } catch (Exception e) {
        }
        try {
            BitMapType.ofHead((byte) 1000);
            fail();
        } catch (Exception e) {
        }
    }
}