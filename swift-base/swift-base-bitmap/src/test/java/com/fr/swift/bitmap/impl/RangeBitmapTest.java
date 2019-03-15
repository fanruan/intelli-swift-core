package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/3/12
 */
public class RangeBitmapTest {

    @Test
    public void of() {
        ImmutableBitMap bitmap = RangeBitmap.of(100, 200);
        assertEquals(100, bitmap.getCardinality());
        for (int i = 100; i < 200; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }

    @Test
    public void toBytes() {
        assertArrayEquals(RangeBitmap.of(1, 3).toBytes(), ByteBuffer.allocate(8).putInt(1).putInt(3).array());
    }
}