package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import junit.framework.TestCase;

import java.util.Random;

/**
 * @author anchore
 */
public class RoaringImmutableTest extends TestCase {
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
        MutableBitMap m = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(m);
        int[] b = prepare(m1);

        ImmutableBitMap and = m.getAnd(m1);

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

    public void testGetOr() {
        MutableBitMap m = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(m);
        int[] b = prepare(m1);

        ImmutableBitMap or = m.getOr(m1);

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

    public void testGetAndNot() {
        MutableBitMap m = getMutableBitMap(),
                m1 = getMutableBitMap();
        int[] a = prepare(m);
        int[] b = prepare(m1);

        ImmutableBitMap andNot = m.getAndNot(m1);

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

    public void testGetNot() {
        MutableBitMap m = getMutableBitMap();
        int[] a = prepare(m);

        ImmutableBitMap not = m.getNot(a.length);
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == 1) == not.contains(i)) {
                fail();
            }
        }
    }

    int rand(int from, int to) {
        return r.nextInt(to - from) + from;
    }
}