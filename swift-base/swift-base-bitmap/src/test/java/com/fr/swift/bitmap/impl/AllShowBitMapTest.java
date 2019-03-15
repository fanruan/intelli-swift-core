package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author anchore
 */
public class AllShowBitMapTest {
    private final Random r = new Random();
    private static final int BOUND = 1000000;

    MutableBitMap getMutableBitMap() {
        return RoaringMutableBitMap.of();
    }

    int[] prepare(MutableBitMap m) {
        int[] a = new int[rand(2, BOUND)];

        for (int i = 0; i < a.length / 2; i++) {
            int ri = r.nextInt(a.length);
            a[ri] = 1;
            m.add(ri);
        }
        return a;
    }

    @Test
    public void testGetAnd() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap and = im.getAnd(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) != and.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetOr() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap or = im.getOr(m);
        for (int i = 0; i < rowCount; i++) {
            if (!or.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap andNot = im.getAndNot(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) == andNot.contains(i)) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testGetNot() {
        int rowCount = r.nextInt(BOUND);
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap not = im.getNot(rowCount);
        if (!not.isEmpty()) {
            Assert.fail();
        }
    }

    int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }

    @Test
    public void of() {
        ImmutableBitMap bitmap = AllShowBitMap.of(100);
        Assert.assertEquals(100, bitmap.getCardinality());
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(bitmap.contains(i));
        }
    }

    @Test
    public void toBytes() {
        assertArrayEquals(AllShowBitMap.of(1).toBytes(), ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(1).array());
    }
}
