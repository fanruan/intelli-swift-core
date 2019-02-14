package com.fr.swift.structure.array;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/20.
 */
public class TestDirectIntList extends TestCase{
    public void testAdd() {
        DirectIntList list = new DirectIntList();
        list.add(0);
        assertEquals(list.get(0), 0);
    }

    public void testGet() {
        DirectIntList list = new DirectIntList(1,1);
        assertEquals(list.get(0), 1);
    }

    public void testSet() {
        DirectIntList list = new DirectIntList(1,0);
        list.set(0,1);
        assertEquals(list.get(0), 1);
    }

    public void testSize() {
        DirectIntList list = new DirectIntList();
        list.add(1);
        assertEquals(list.size(), 1);
    }

    public void testClear() {
        DirectIntList list = new DirectIntList();
        list.add(1);
        list.clear();
        assertEquals(list.size(), 0);
        try{
            list.get(0);
            assert false;
        } catch (Exception e){
            //do nothing
        }
    }

}