package com.fr.swift.structure.array;

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

}