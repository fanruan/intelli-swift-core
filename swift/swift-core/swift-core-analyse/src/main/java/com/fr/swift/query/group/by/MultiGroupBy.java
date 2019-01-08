package com.fr.swift.query.group.by;

import com.fr.swift.query.group.by2.row.MultiGroupByRowIterator;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/3/28.
 */
abstract class MultiGroupBy<T> implements Iterator<Pair<T, RowTraversal[]>> {

    private Iterator<GroupByEntry[]> iterator;

    public MultiGroupBy(GroupByInfo groupByInfo) {
        this.iterator = new MultiGroupByRowIterator(groupByInfo);
    }

    /**
     * 如果迭代器用于多个segment合并，int[]类型的key要转为globalIndex，而Object[]类型的key则不需要
     *
     * @param indexes
     * @return
     */
    protected abstract T createKey(int[] indexes);

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Pair<T, RowTraversal[]> next() {
        GroupByEntry[] row = iterator.next();
        int[] indexes = new int[row.length];
        for (int i = 0; i < row.length; i++) {
            indexes[i] = row[i].getIndex();
        }
        return Pair.of(createKey(indexes), create(row));
    }

    private RowTraversal[] create(GroupByEntry[] row) {
        RowTraversal[] traversals = new RowTraversal[row.length];
        for (int i = 0; i < row.length; i++) {
            traversals[i] = row[i].getTraversal();
        }
        return traversals;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
