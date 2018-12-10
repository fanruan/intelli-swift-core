package com.fr.swift.structure.array;

import junit.framework.TestCase;
import org.junit.Ignore;

/**
 * Created by Lyon on 2017/12/5.
 */
public class TestRangeIntList extends TestCase {

    public void testConstructor() {
        try {
            RangeIntList list = new RangeIntList(2, 1);
            assert false;
        } catch (Exception e) {
        }
    }

    public void testSize() {
        RangeIntList list = new RangeIntList(2, 3);
        assertEquals(2, list.size());
        list = new RangeIntList(2, 2);
        assertEquals(1, list.size());
    }

    @Ignore
    public void testGet() {
//        RangeIntList list = new RangeIntList(2, 5);
//        int[] array = new int[] {2, 3, 4, 5};
//        IntStream.range(0, list.size()).forEach(i -> assertEquals(list.get(i), array[i]));
    }
}
