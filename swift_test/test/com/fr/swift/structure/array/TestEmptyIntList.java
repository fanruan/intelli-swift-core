package com.fr.swift.structure.array;

import junit.framework.TestCase;

/**
 * Created by Lyon on 2017/12/5.
 */
public class TestEmptyIntList extends TestCase {

    public void testSize() {
        EmptyIntList intList = new EmptyIntList();
        assertEquals(0, intList.size());
    }

    public void testGet() {
        EmptyIntList intList = new EmptyIntList();
        try {
            intList.get(0);
            assert false;
        } catch (Exception e) {
        }
    }
}
