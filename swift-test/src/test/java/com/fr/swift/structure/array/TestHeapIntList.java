package com.fr.swift.structure.array;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/23.
 */
public class TestHeapIntList extends TestCase {
    public void testAdd() throws Exception {
        HeapIntList list = new HeapIntList();
        list.add(1);
        assertEquals(list.get(0), 1);
    }

    public void testGet() throws Exception {
        HeapIntList list = new HeapIntList(1, 1);
        assertEquals(list.get(0), 1);
    }

    public void testSize() throws Exception {
        HeapIntList list = new HeapIntList();
        list.add(1);
        assertEquals(list.size(), 1);
    }

    public void testSet() throws Exception {
        HeapIntList list = new HeapIntList(1, 1);
        list.set(0, 2);
        assertEquals(list.get(0), 2);
    }

    public void testClear() throws Exception {
        HeapIntList list = new HeapIntList(1, 1);
        list.clear();
        assertEquals(list.size(), 0);
        try {
            list.get(0);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    public void testToArray() throws Exception {
        HeapIntList list = new HeapIntList(1, 1);
        list.add(2);
        int[] array = list.toArray();
        assertEquals(array[0], 1);
        assertEquals(array[1], 2);
        assertEquals(array.length, 2);
    }

}