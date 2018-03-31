package com.fr.swift.structure.queue;

import com.fr.swift.query.aggregator.Combiner;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingUtilsTest extends TestCase {

    public void testMerge() {
        int listCount = 8;
        int listSize = 10;
        List<List<AtomicInteger>> lists = new ArrayList<>();
        for (int i = 0; i < listCount; i++) {
            lists.add(new ArrayList<>());
        }
        for (int i = 0; i < listCount; i++) {
            for (int j = 0; j < listSize; j++) {
                lists.get(i).add(new AtomicInteger(i + j + 1));
            }
        }
        Comparator<AtomicInteger> comparator = new Comparator<AtomicInteger>() {
            @Override
            public int compare(AtomicInteger o1, AtomicInteger o2) {
                Integer a = o1.intValue();
                Integer b = o2.intValue();
                return a.compareTo(b);
            }
        };
        TreeMap<Integer, Integer> map = new TreeMap(Comparator.naturalOrder());
        for (int i = 0; i < listCount; i++) {
            for (int j = 0; j < listSize; j++) {
                int key = lists.get(i).get(j).intValue();
                int value = key;
                if (map.containsKey(key)) {
                    value += map.get(key);
                }
                map.put(key, value);
            }
        }
        List<Integer> expected = new ArrayList<>(map.values());
        Combiner<AtomicInteger> combiner = new Combiner<AtomicInteger>() {
            @Override
            public void combine(AtomicInteger current, AtomicInteger other) {
                current.getAndAdd(other.intValue());
            }
        };
        List<AtomicInteger> actual = SortedListMergingUtils.merge(lists, comparator, combiner);
        AtomicInteger i = new AtomicInteger(1);
        int b = i.addAndGet(1);
        boolean c = i.get() == 2;
    }
}
