package com.fr.swift.structure.queue;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lyon on 2018/3/30.
 */
public class SortedListMergingUtilsTest extends TestCase {

    @Ignore
    public void testMerge() {
        int size = 10;
        int numberOfList = 3;
        List<List<Pair<Integer, Integer>>> sortedLists = new ArrayList<List<Pair<Integer, Integer>>>();
        for (int i = 0; i < numberOfList; i++) {
            List<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();
            for (int j = 0; j < size; j++) {
                list.add(Pair.of(j, j));
            }
            sortedLists.add(list);
        }
        Comparator<Pair<Integer, Integer>> comparator = new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return Comparators.<Integer>asc().compare(o1.getKey(), o2.getKey());
            }
        };
        Combiner<Pair<Integer, Integer>> combiner = new Combiner<Pair<Integer, Integer>>() {
            @Override
            public void combine(Pair<Integer, Integer> current, Pair<Integer, Integer> other) {
                current.setValue(current.getValue() + other.getValue());
            }
        };
        List<Pair<Integer, Integer>> result = SortedListMergingUtils.merge(sortedLists, comparator, combiner);
        assertEquals(size, result.size());
        for (int i = 0; i < size; i++) {
            int expected = i * numberOfList;
            int actual = result.get(i).getValue();
            assertEquals(expected, actual);
        }
    }
}
