package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2019/3/12
 */
public class RoaringMutableBitMapTest {

    @Test
    public void ofBuffer() {
        int[] ints = {1, 3, 4, 5, 8, 9};
        MutableRoaringBitmap roaringBitmap = MutableRoaringBitmap.bitmapOf(ints);
        ByteBuffer buf = ByteBuffer.wrap(RoaringMutableBitMap.of(roaringBitmap).toBytes());
        buf.rewind();

        MutableBitMap bitmap = RoaringMutableBitMap.ofBuffer(buf);
        assertEquals(ints.length, bitmap.getCardinality());
        for (int i : ints) {
            bitmap.contains(i);
        }
    }
}