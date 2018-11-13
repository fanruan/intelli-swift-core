package com.fr.swift.query.group.by;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/3/28.
 */
abstract class MultiGroupBy<T> implements Iterator<KeyValue<RowIndexKey<T>, RowTraversal[]>> {

    protected List<Column> dimensions;
    private Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator;
    private RowTraversal[] traversals;

    public MultiGroupBy(List<Column> dimensions, DetailFilter detailFilter, int[] cursor, boolean[] asc) {
        this.dimensions = dimensions;
        this.iterator = new MultiDimensionGroupBy(dimensions, detailFilter, cursor, asc);
        this.traversals = new RowTraversal[dimensions.size() + 1];
    }

    /**
     * 如果迭代器用于多个segment合并，int[]类型的key要转为globalIndex，而Object[]类型的key则不需要
     *
     * @param indexes
     * @return
     */
    protected abstract RowIndexKey<T> createKey(int[] indexes);

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public KeyValue<RowIndexKey<T>, RowTraversal[]> next() {
        int[] key = new int[0];
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, RowTraversal> kv = iterator.next();
            int[] indexes = kv.getKey().getKey();
            int index = currentIndex(indexes);
            updateTraversal(index, kv.getValue());
            if (isRow(indexes)) {
                key = indexes;
                break;
            }
        }
        // traversal数组被迭代器复用的，所以返回的时候要拷贝一个引用数组
        return new KeyValue<RowIndexKey<T>, RowTraversal[]>(createKey(key), Arrays.copyOf(traversals, traversals.length));
    }

    private boolean isRow(int[] key) {
        if (key.length == 0) {
            return true;
        }
        return key[key.length - 1] != -1;
    }

    private int currentIndex(int[] indexes) {
        for (int i = indexes.length - 1; i >= 0; i--) {
            if (indexes[i] != -1) {
                // 因为RowTraversal[]第0个位置保存了汇总行索引
                return i + 1;
            }
        }
        return 0;
    }

    private void updateTraversal(int index, RowTraversal traversal) {
        traversals[index] = traversal;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
