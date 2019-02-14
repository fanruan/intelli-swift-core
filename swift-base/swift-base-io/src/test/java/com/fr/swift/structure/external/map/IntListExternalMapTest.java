package com.fr.swift.structure.external.map;

import com.fr.swift.compare.Comparators;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.intlist.IntListExternalMapFactory;
import com.fr.swift.test.TestResource;
import com.fr.swift.util.FileUtil;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/3/5
 */
public class IntListExternalMapTest extends TestCase {
    private List<Integer>
            list1 = Arrays.asList(4, 5, 6),
            list2 = Arrays.asList(1, 2, 3),
            list3 = Arrays.asList(6, 7, 7),
            list4 = Arrays.asList(0, 1, 2);

    private String basePath = TestResource.getRunPath(getClass());

    public void testLongPutThenGet() {
        ExternalMap<Long, IntList> map =
                IntListExternalMapFactory.getIntListExternalMap(ClassType.LONG, Comparators.<Long>asc(), basePath + "/externalMapTest/long", false);
        map.put(1L, toIntList(list1));
        map.put(0L, toIntList(list2));
        map.put(3L, toIntList(list3));
        map.dumpMap();
        map.put(1L, toIntList(list4));
        map.dumpMap();

        Iterator<Entry<Long, IntList>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<Long, IntList> entry = itr.next();
        assertEquals(entry.getKey().longValue(), 0);
        assertEquals(list2, toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().longValue(), 1);
        assertEquals(Arrays.asList(4, 5, 6, 0, 1, 2), toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().longValue(), 3);
        assertEquals(list3, toList(entry.getValue()));

        map.release();
    }

    @Ignore
    public void testIntegerPutThenGet() {
        ExternalMap<Integer, IntList> map =
                IntListExternalMapFactory.getIntListExternalMap(ClassType.INTEGER, Comparators.<Integer>asc(), basePath + "/externalMapTest/Integer", false);
        map.put(1, toIntList(list1));
        map.put(0, toIntList(list2));
        map.put(3, toIntList(list3));
        map.dumpMap();
        map.put(1, toIntList(list4));
        map.dumpMap();

        Iterator<Entry<Integer, IntList>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<Integer, IntList> entry = itr.next();
        assertEquals(entry.getKey().intValue(), 0);
        assertEquals(list2, toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().intValue(), 1);
        assertEquals(Arrays.asList(4, 5, 6, 0, 1, 2), toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey().intValue(), 3);
        assertEquals(list3, toList(entry.getValue()));

        map.release();
    }

    public void testDoublePutThenGet() {
        ExternalMap<Double, IntList> map =
                IntListExternalMapFactory.getIntListExternalMap(ClassType.DOUBLE, Comparators.<Double>asc(), basePath + "/externalMapTest/long", false);
        map.put(1D, toIntList(list1));
        map.put(0D, toIntList(list2));
        map.put(3D, toIntList(list3));
        map.dumpMap();
        map.put(1D, toIntList(list4));
        map.dumpMap();

        Iterator<Entry<Double, IntList>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<Double, IntList> entry = itr.next();
        assertEquals(entry.getKey(), 0D);
        assertEquals(list2, toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), 1D);
        assertEquals(Arrays.asList(4, 5, 6, 0, 1, 2), toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), 3D);
        assertEquals(list3, toList(entry.getValue()));

        map.release();
    }

    public void testStringPutThenGet() {
        ExternalMap<String, IntList> map =
                IntListExternalMapFactory.getIntListExternalMap(ClassType.STRING, Comparators.<String>asc(), basePath + "/externalMapTest/string", false);
        map.put("1", toIntList(list1));
        map.put("0", toIntList(list2));
        map.put("3", toIntList(list3));
        map.dumpMap();
        map.put("1", toIntList(list4));
        map.dumpMap();

        Iterator<Entry<String, IntList>> itr = map.getIterator();

        assertTrue(itr.hasNext());
        Map.Entry<String, IntList> entry = itr.next();
        assertEquals(entry.getKey(), "0");
        assertEquals(list2, toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), "1");
        assertEquals(Arrays.asList(4, 5, 6, 0, 1, 2), toList(entry.getValue()));

        assertTrue(itr.hasNext());
        entry = itr.next();
        assertEquals(entry.getKey(), "3");
        assertEquals(list3, toList(entry.getValue()));

        map.release();
    }

    private static IntList toIntList(List<Integer> list) {
        IntList ints = IntListFactory.createIntList(list.size());
        for (Integer integer : list) {
            ints.add(integer);
        }
        return ints;
    }

    private static List<Integer> toList(IntList ints) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < ints.size(); i++) {
            list.add(ints.get(i));
        }
        return list;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        FileUtil.delete(basePath + "/externalMapTest");
    }
}