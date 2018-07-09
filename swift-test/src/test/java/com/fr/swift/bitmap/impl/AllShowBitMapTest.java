package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import junit.framework.TestCase;

import java.util.Random;

/**
 * @author anchore
 */
public class AllShowBitMapTest extends TestCase {
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

    public void testGetAnd() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap and = im.getAnd(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) != and.contains(i)) {
                fail();
            }
        }
    }

    public void testGetOr() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap or = im.getOr(m);
        for (int i = 0; i < rowCount; i++) {
            if (!or.contains(i)) {
                fail();
            }
        }
    }

    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        int rowCount = a.length << 2;
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap andNot = im.getAndNot(m);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) == andNot.contains(i)) {
                fail();
            }
        }
    }

    public void testGetNot() {
        int rowCount = r.nextInt(BOUND);
        ImmutableBitMap im = AllShowBitMap.of(rowCount);

        ImmutableBitMap not = im.getNot(rowCount);
        if (!not.isEmpty()) {
            fail();
        }
    }

    int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }
}
