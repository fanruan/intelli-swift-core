package com.fr.swift.query.group.by.paging;

import com.fr.swift.result.KeyValue;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;

public class GroupByPagingIterator implements Iterator<KeyValue<int[], RowTraversal>> {

    private final int threshold;
    private int counter = 0;
    private Iterator<KeyValue<int[], RowTraversal>> groupByIterator;

    public GroupByPagingIterator(int threshold, Iterator<KeyValue<int[], RowTraversal>> groupByIterator) {
        this.threshold = threshold;
        this.groupByIterator = groupByIterator;
    }

    public boolean hasNext() {
        return counter < threshold && groupByIterator.hasNext();
    }

    @Override
    public KeyValue<int[], RowTraversal> next() {
        KeyValue<int[], RowTraversal> next = groupByIterator.next();
        int[] key = next.getKey();
        if (key[key.length - 1] != -1) {
            // 这边只对普通行进行计数
            counter += 1;
        }
        return groupByIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
