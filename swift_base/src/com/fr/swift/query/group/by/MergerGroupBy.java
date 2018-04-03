package com.fr.swift.query.group.by;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.queue.SortedListMergingUtils;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/3/29.
 * 返回多个segment合并之后的结果，按传入的segment的MultiGroupBy的顺序返回
 */
public abstract class MergerGroupBy<T> implements Iterator<KeyValue<RowIndexKey<T>, List<RowTraversal[]>>> {

    private MultiGroupBy<T>[] iterators;
    //升序还是降序
    protected final boolean[] asc;
    private Iterator<KeyValue<RowIndexKey<T>, RowTraversal[]>> mergeIt;
    private KeyValue<RowIndexKey<T>, RowTraversal[]> current = null;


    public MergerGroupBy(MultiGroupBy<T>[] iterators, boolean[] asc) {
        Util.requireNonNull(iterators);
        Util.requireNonNull(asc);
        this.iterators = iterators;
        this.asc = asc;
    }

    /**
     * 初始化迭代器，给子类调用
     */
    protected void init() {
        mergeIt = SortedListMergingUtils.mergeIterator(array2List(iterators), getComparator());
    }

    protected abstract Comparator<KeyValue<RowIndexKey<T>, RowTraversal[]>> getComparator();

    @Override
    public boolean hasNext() {
        return mergeIt.hasNext() || current != null;
    }

    @Override
    public KeyValue<RowIndexKey<T>, List<RowTraversal[]>> next() {
        List<RowTraversal[]> values = new ArrayList<RowTraversal[]>();
        RowIndexKey<T> key = current != null ? current.getKey() : null;
        if (current != null) {
            values.add(current.getValue());
        }
        // 重置current
        current = null;
        while (mergeIt.hasNext()) {
            KeyValue<RowIndexKey<T>, RowTraversal[]> kv = mergeIt.next();
            if (key == null) {
                key = kv.getKey();
                values.add(kv.getValue());
                continue;
            }
            if (getComparator().compare(new KeyValue<RowIndexKey<T>, RowTraversal[]>(key, null), kv) == 0) {
                // key相同，收集value
                values.add(kv.getValue());
                continue;
            }
            // key != null且key != kv.getKey()，这个时候返回当前key对应的值，并设置kv为下一次的current
            current = kv;
            break;
        }
        return new KeyValue<RowIndexKey<T>, List<RowTraversal[]>>(key, values);
    }

    private List<Iterator<KeyValue<RowIndexKey<T>, RowTraversal[]>>> array2List(MultiGroupBy<T>[] its) {
        List<Iterator<KeyValue<RowIndexKey<T>, RowTraversal[]>>> iterators = new ArrayList<Iterator<KeyValue<RowIndexKey<T>, RowTraversal[]>>>();
        for (int i = 0; i < its.length; i++) {
            iterators.add(its[i]);
        }
        return iterators;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
