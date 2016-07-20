package com.fr.bi.stable.structure.collection.map;


import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2016/7/20.
 */
public class IntMapTest extends TestCase {
    public void testMap() throws Exception {
        IntMap map = new IntMap();
        Object ob = new Object();
        map.put(0, ob);
        assertEquals(map.get(0), ob);
        assertEquals(map.size, 1);
        assertEquals(map.containsKey(0), true);
    }

}