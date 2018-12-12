package com.fr.swift.structure.array;

import com.fr.swift.structure.IntIterable.IntIterator;
import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/20.
 */
public class TestDirectIntArray extends TestCase {
    public void testSize() {
        DirectIntArray array = new DirectIntArray(2);
        assertEquals(array.size(), 2);
        array.release();
    }

    public void testPut() {
        DirectIntArray array = new DirectIntArray(1);
        array.put(0, 2);
        assertEquals(array.get(0), 2);
        array.release();
    }

    public void testGet() {
        DirectIntArray array = new DirectIntArray(2, 2);
        assertEquals(array.get(0), 2);
        array.release();
    }

    public void testRelease() {
        DirectIntArray array = new DirectIntArray(2, 2);
        array.release();
        try {
            array.get(0);
            assert false;
        } catch (Exception e) {
            //do nothing
        }

    }

    public void testIterate() {
        IntArray ints = new DirectIntArray(2, 2);
        for (Integer anInt : ints) {
            assertEquals(2, anInt.intValue());
        }

        IntIterator intItr = ints.intIterator();
        while (intItr.hasNext()) {
            assertEquals(2, intItr.nextInt());
        }
    }
}