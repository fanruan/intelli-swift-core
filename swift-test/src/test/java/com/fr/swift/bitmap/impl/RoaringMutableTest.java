package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;
import junit.framework.TestCase;

import java.util.Random;

/**
 * @author anchore
 */
public class RoaringMutableTest extends TestCase {
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

    public void testOr() {
        MutableBitMap or = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(or),
                b = prepare(m1);

        or.or(m1);

        int[] longer = a.length > b.length ? a : b,
                shorter = longer == a ? b : a;

        for (int i = 0; i < shorter.length; i++) {
            int result = a[i] + b[i];
            if (result > 0 != or.contains(i)) {
                fail();
            }
        }

        for (int i = shorter.length; i < longer.length; i++) {
            if ((longer[i] == 1) != or.contains(i)) {
                fail();
            }
        }
    }

    public void testAnd() {
        MutableBitMap and = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(and),
                b = prepare(m1);

        and.and(m1);

        int[] longer = a.length > b.length ? a : b,
                shorter = longer == a ? b : a;

        for (int i = 0; i < shorter.length; i++) {
            int result = a[i] & b[i];
            if ((result == 1) != and.contains(i)) {
                fail();
            }
        }

        for (int i = shorter.length; i < longer.length; i++) {
            if (and.contains(i)) {
                fail();
            }
        }
    }

    public void testAndNot() {
        MutableBitMap andNot = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(andNot),
                b = prepare(m1);

        andNot.andNot(m1);

        int[] longer = a.length > b.length ? a : b,
                shorter = longer == a ? b : a;

        for (int i = 0; i < shorter.length; i++) {
            int result = a[i] & ~b[i];
            if ((result == 1) != andNot.contains(i)) {
                fail();
            }
        }

        for (int i = shorter.length; i < longer.length; i++) {
            if (longer == b) {
                if (andNot.contains(i)) {
                    fail();
                }
            } else if ((longer[i] == 1) != andNot.contains(i)) {
                fail();
            }
        }
    }

    public void testAdd() {
        MutableBitMap m = getMutableBitMap();
        int i = r.nextInt(BOUND);

        if (m.contains(i)) {
            fail();
        }
        m.add(i);
        if (!m.contains(i)) {
            fail();
        }
    }

    public void testRemove() {
        MutableBitMap m = getMutableBitMap();
        int i = r.nextInt(BOUND);

        m.add(i);
        m.remove(i);
        if (m.contains(i)) {
            fail();
        }
    }

    int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }
}
