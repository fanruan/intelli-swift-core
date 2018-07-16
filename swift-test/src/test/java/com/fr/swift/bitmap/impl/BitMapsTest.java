package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import junit.framework.TestCase;

/**
 * Created by Lyon on 2018/6/15.
 */
public class BitMapsTest extends TestCase {

    public void test2Array() {
        IntList list = IntListFactory.createIntList(3);
        list.add(2);
        list.add(4);
        list.add(6);
        ImmutableBitMap bitMap = BitMaps.newImmutableBitMap(list);
        IntArray array = BitMaps.traversal2Array(bitMap);
        assertEquals(array.get(0), 2);
        assertEquals(array.get(1), 4);
        assertEquals(array.get(2), 6);
    }
}
