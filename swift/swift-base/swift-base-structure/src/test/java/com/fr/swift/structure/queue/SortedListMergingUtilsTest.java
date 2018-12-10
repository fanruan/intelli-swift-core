package com.fr.swift.structure.queue;

import junit.framework.TestCase;
import org.junit.Ignore;

/**
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingUtilsTest extends TestCase {

    @Ignore
    public void testMerge() {
//        int listCount = 6;
//        int listSize = 10;
//        List<List<AtomicInteger>> lists = new ArrayList<List<AtomicInteger>>();
//        for (int i = 0; i < listCount; i++) {
//            lists.add(new ArrayList<AtomicInteger>());
//        }
//        for (int i = 0; i < listCount; i++) {
//            for (int j = 0; j < listSize; j++) {
//                lists.get(i).add(new AtomicInteger(i + j + 1));
//            }
//        }
//        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Comparators.<Integer>asc());
//        for (int i = 0; i < listCount; i++) {
//            for (int j = 0; j < listSize; j++) {
//                int key = lists.get(i).get(j).intValue();
//                int value = key;
//                if (map.containsKey(key)) {
//                    // 相同值加起来
//                    value += map.get(key);
//                }
//                map.put(key, value);
//            }
//        }
//        List<Integer> expected = new ArrayList<Integer>(map.values());
//        Comparator<AtomicInteger> comparator = new Comparator<AtomicInteger>() {
//            @Override
//            public int compare(AtomicInteger o1, AtomicInteger o2) {
//                Integer a = o1.intValue();
//                Integer b = o2.intValue();
//                return a.compareTo(b);
//            }
//        };
//        Combiner<AtomicInteger> combiner = new Combiner<AtomicInteger>() {
//            @Override
//            public void combine(AtomicInteger current, AtomicInteger other) {
//                current.getAndAdd(other.intValue());
//            }
//        };
//        List<AtomicInteger> actual = SortedListMergingUtils.merge(lists, comparator, combiner);
//        assertEquals(expected.size(), actual.size());
//        IntStream.range(0, expected.size()).forEach(i -> assertTrue(expected.get(i) == actual.get(i).intValue()));
    }
}
