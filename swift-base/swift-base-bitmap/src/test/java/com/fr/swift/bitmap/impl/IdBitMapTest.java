package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * @author anchore
 */
public class IdBitMapTest {
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
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap and = im.getAnd(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id && and.contains(i)) {
                fail();
            }
        }
        int tmp = id < a.length ? a[id] : 0;
        if ((tmp == 1) != and.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetOr() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap or = im.getOr(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id) {
                if ((a[i] == 1) != or.contains(i)) {
                    fail();
                }
            }
        }
        if (!or.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int id = r.nextInt(BOUND);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap andNot = im.getAndNot(m);
        for (int i = 0; i < a.length; i++) {
            if (i != id && andNot.contains(i)) {
                fail();
            }
        }

        if (m.contains(id) == andNot.contains(id)) {
            fail();
        }
    }

    @Test
    public void testGetNot() {
        int rowCount = r.nextInt(BOUND);
        int id = r.nextInt(rowCount);
        ImmutableBitMap im = IdBitMap.of(id);

        ImmutableBitMap not = im.getNot(rowCount);
        for (int i = 0; i < rowCount; i++) {
            if (i != id && !not.contains(i)) {
                fail();
            }
        }
        if (not.contains(id)) {
            fail();
        }
    }

    int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }

    @Test
    public void toBytes() {
        assertArrayEquals(IdBitMap.of(1).toBytes(), ByteBuffer.allocate(4).putInt(1).array());
    }
}
