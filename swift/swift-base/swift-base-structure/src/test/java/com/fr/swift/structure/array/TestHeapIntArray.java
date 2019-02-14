package com.fr.swift.structure.array;

import com.fr.swift.structure.IntIterable.IntIterator;
import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/20.
 */
public class TestHeapIntArray extends TestCase {
    public void testPut() {
        HeapIntArray array = new HeapIntArray(1,1);
        array.put(0, 0);
        assertEquals(array.get(0), 0);
    }

    public void testSize() {
        HeapIntArray array = new HeapIntArray(1,1);
        assertEquals(array.size(), 1);
    }

    public void testGet() {
        HeapIntArray array = new HeapIntArray(1,1);
        assertEquals(array.get(0), 1);
    }

    public void testRelease() {
        HeapIntArray array = new HeapIntArray(1,1);
        array.release();
        try {
            array.get(0);
            assert false;
        } catch (Exception e){
            assert true;
        }
    }

    public void testIterate() {
        IntArray ints = new HeapIntArray(2, 3);
        for (Integer anInt : ints) {
            assertEquals(3, anInt.intValue());
        }

        IntIterator intItr = ints.intIterator();
        while (intItr.hasNext()) {
            assertEquals(3, intItr.nextInt());
        }
    }

}