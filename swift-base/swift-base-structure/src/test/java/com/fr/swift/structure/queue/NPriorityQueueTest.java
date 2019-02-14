package com.fr.swift.structure.queue;

import com.fr.swift.compare.Comparators;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Lyon on 2018/6/22.
 */
public class NPriorityQueueTest extends TestCase {

    public void test() {
        // topN用最小堆
        NPriorityQueue<Integer> queue = new NPriorityQueue<Integer>(3, Comparators.<Integer>asc());
        queue.add(1);
        queue.add(2);
        queue.add(1);
        queue.add(5);
        queue.add(3);
        List<Integer> top3 = queue.toList();
        assertEquals(top3.size(), 3);
        assertTrue(top3.get(0) == 5);
        assertTrue(top3.get(1) == 3);
        assertTrue(top3.get(2) == 2);

        // 求bottomN用最大堆
        queue = new NPriorityQueue<Integer>(3, Comparators.<Integer>desc());
        queue.add(1);
        queue.add(2);
        queue.add(1);
        queue.add(5);
        queue.add(3);
        top3 = queue.toList();
        assertEquals(top3.size(), 3);
        assertTrue(top3.get(0) == 1);
        assertTrue(top3.get(1) == 1);
        assertTrue(top3.get(2) == 2);
    }
}
