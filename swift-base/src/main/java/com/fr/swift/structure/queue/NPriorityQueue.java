package com.fr.swift.structure.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 用于topN的堆排序
 * Created by Lyon on 2018/6/22.
 */
public class NPriorityQueue<T> {

    private int maxSize;
    private Comparator<T> comparator;
    private PriorityQueue<T> queue;

    /**
     * 使用类库的java.util.PriorityQueue的堆实现，复杂度O(n*logN)
     * 使用注意事项topN用最小堆(升序比较器)，bottomN用最大堆(降序比较器)
     *
     * @param maxSize
     * @param comparator
     */
    public NPriorityQueue(int maxSize, Comparator<T> comparator) {
        this.maxSize = maxSize;
        this.comparator = comparator;
        this.queue = new PriorityQueue<T>(maxSize, comparator);
    }

    public void add(T element) {
        if (queue.size() < maxSize) {
            queue.add(element);
        } else {
            T peek = queue.peek();
            if (comparator.compare(element, peek) > 0) {
                queue.poll();
                queue.add(element);
            }
        }
    }

    public List<T> toList() {
        List<T> list = new ArrayList<T>();
        while (!queue.isEmpty()) {
            list.add(queue.poll());
        }
        Collections.reverse(list);
        return list;
    }
}
