package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/3/26
 */
public class EmptyBitmapTest {

    @Test
    public void toBytes() {
        assertArrayEquals(new byte[]{BitMapType.EMPTY.getHead()}, new EmptyBitmap().toBytes());
    }

    @Test
    public void getType() {
        assertEquals(BitMapType.EMPTY, new EmptyBitmap().getType());
    }
}