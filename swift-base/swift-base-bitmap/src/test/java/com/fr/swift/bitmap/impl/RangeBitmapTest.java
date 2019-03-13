package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author anchore
 * @date 2019/3/12
 */
public class RangeBitmapTest {

    @Test
    public void ofBuffer() {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putInt(100).putInt(200).rewind();

        ImmutableBitMap bitmap = RangeBitmap.ofBuffer(buf);
        Assert.assertEquals(100, bitmap.getCardinality());
        for (int i = 100; i < 200; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }
}