package com.fr.swift.structure.external.map;

import com.fr.swift.compare.Comparators;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.intpairs.IntPairsExtMaps;
import com.fr.swift.test.TestResource;
import com.fr.swift.util.FileUtil;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/1/5
 */
public class IntPairsExtMapTest extends TestCase {
    List<IntPair>
            list1 = Arrays.asList(of(0, 4), of(1, 5), of(2, 6)),
            list2 = Arrays.asList(of(0, 1), of(1, 2), of(2, 3)),
            list3 = Arrays.asList(of(0, 6), of(1, 7), of(2, 7)),
            list4 = Arrays.asList(of(3, 0), of(4, 1), of(5, 2));

    String basePath = TestResource.getRunPath(getClass());

    public void testLongPutThenGet() {
        ExternalMap<Long, List<IntPair>> map =
                IntPairsExtMaps.newExternalMap(ClassType.LONG, Comparators.<Long>asc(), basePath + "/externalMapTest/long");
        map.put(1L, list1);
        map.put(0L, list2);
        map.put(3L, list3);
        map.dumpMap();
        map.put(1L, list4);
        map.dumpMap();

        Iterator<Map.Entry<Long, List<IntPair>>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<Long, List<IntPair>> entry = itr.next();
        assertEquals(entry.getKey().longValue(), 0);
        assertEquals(list2, entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().longValue(), 1);
        assertEquals(Arrays.asList(
                of(0, 4),
                of(1, 5),
                of(2, 6),
                of(3, 0),
                of(4, 1),
                of(5, 2)), entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().longValue(), 3);
        assertEquals(list3, entry.getValue());

        map.release();
    }

    public void testDoublePutThenGet() {
        ExternalMap<Double, List<IntPair>> map =
                IntPairsExtMaps.newExternalMap(ClassType.DOUBLE, Comparators.<Double>asc(), basePath + "/externalMapTest/double");
        map.put(1D, list1);
        map.put(0D, list2);
        map.put(3D, list3);
        map.dumpMap();
        map.put(1D, list4);
        map.dumpMap();

        Iterator<Map.Entry<Double, List<IntPair>>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<Double, List<IntPair>> entry = itr.next();
        assertEquals(Double.compare(entry.getKey(), 0D), 0);
        assertEquals(list2, entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(Double.compare(entry.getKey(), 1D), 0);
        assertEquals(Arrays.asList(
                of(0, 4),
                of(1, 5),
                of(2, 6),
                of(3, 0),
                of(4, 1),
                of(5, 2)), entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(Double.compare(entry.getKey(), 3D), 0);
        assertEquals(list3, entry.getValue());

        map.clear();
    }

    public void testStringPutThenGet() {
        ExternalMap<String, List<IntPair>> map =
                IntPairsExtMaps.newExternalMap(ClassType.STRING, Comparators.<String>asc(), basePath + "/externalMapTest/String");
        map.put("1", list1);
        map.put("0", list2);
        map.put("3", list3);
        map.dumpMap();
        map.put("1", list4);
        map.dumpMap();

        Iterator<Map.Entry<String, List<IntPair>>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<String, List<IntPair>> entry = itr.next();
        assertEquals(entry.getKey(), "0");
        assertEquals(list2, entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), "1");
        assertEquals(Arrays.asList(
                of(0, 4),
                of(1, 5),
                of(2, 6),
                of(3, 0),
                of(4, 1),
                of(5, 2)), entry.getValue());

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), "3");
        assertEquals(list3, entry.getValue());

        map.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        FileUtil.delete(basePath + "/externalMapTest");
    }

    private static IntPair of(int key, int val) {
        return IntPair.of(key, val);
    }
}