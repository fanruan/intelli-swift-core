package com.fr.swift.query.group.by.paging;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;

public class GroupByPagingIterator implements Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> {

    private final int threshold;
    private int counter = 0;
    private Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> groupByIterator;

    public GroupByPagingIterator(int threshold, Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> groupByIterator) {
        this.threshold = threshold;
        this.groupByIterator = groupByIterator;
    }

    public boolean hasNext() {
        return counter < threshold && groupByIterator.hasNext();
    }

    @Override
    public KeyValue<RowIndexKey<int[]>, RowTraversal> next() {
        KeyValue<RowIndexKey<int[]>, RowTraversal> next = groupByIterator.next();
        int[] key = next.getKey().getKey();
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
