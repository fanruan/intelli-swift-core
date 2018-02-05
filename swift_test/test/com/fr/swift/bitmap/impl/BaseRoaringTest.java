package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import junit.framework.TestCase;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author anchore
 */
public class BaseRoaringTest extends TestCase {
    private final Random r = new Random();
    private static final int BOUND = 1000000;

    MutableBitMap getMutableBitMap() {
        return RoaringMutableBitMap.newInstance();
    }

    ImmutableBitMap getImmutableBitMap() {
        return RoaringImmutableBitMap.newInstance();
    }

    int[] prepare(MutableBitMap m) {
        int[] a = new int[BOUND];
        for (int i = 0; i < a.length / 2; i++) {
            int ri = r.nextInt(a.length);
            a[ri] = 1;
            m.add(ri);
        }
        return a;
    }

    public void testContains() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);

        m.traversal(row -> {
            if (a[row] == 0) {
                fail();
            }
        });

        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1 ? !m.contains(i) : m.contains(i)) {
                fail();
            }
        }
    }

    public void testIsEmpty() {
        ImmutableBitMap im = getImmutableBitMap();
        assertTrue(im.isEmpty());

        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);

        im = m.clone();
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1) {
                m.remove(i);
            }
        }

        assertTrue(!im.isEmpty());
        assertTrue(m.isEmpty());
    }

    public void testTraversal() {
        MutableBitMap m = getMutableBitMap();
        final int[] a = prepare(m);

        m.traversal(row -> {
            if (a[row] == 0) {
                fail();
            }
        });
    }

    public void testBreakableTraversal() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);

        final int breakIndex = r.nextBoolean() ? -1 : a[r.nextInt(BOUND)];
        final int[] rowWrap = new int[]{-1};
        if (m.breakableTraversal(row -> {
            if (breakIndex == row) {
                rowWrap[0] = row;
                return true;
            }
            return false;
        })) {
            assertTrue(rowWrap[0] != -1 && rowWrap[0] == breakIndex);
        } else {
            assertTrue(rowWrap[0] == -1);
        }
    }

    public void testGetCardinality() {
        MutableBitMap m = getMutableBitMap();
        final int[] a = prepare(m);

        assertTrue((IntStream.of(a).sum()) == m.getCardinality());
    }

    public void testToBytes() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);
        byte[] bytes = m.toBytes();
        MutableBitMap m1 = RoaringMutableBitMap.fromBytes(bytes);
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1 ? !m1.contains(i) : m1.contains(i)) {
                fail();
            }
        }

        ImmutableBitMap im = m.clone();
        bytes = im.toBytes();
        ImmutableBitMap m2 = RoaringImmutableBitMap.fromBytes(bytes);
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1 ? !m2.contains(i) : m2.contains(i)) {
                fail();
            }
        }
    }
}