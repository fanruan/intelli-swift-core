package com.fr.swift.query.group.by;

import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.queue.SortedListMergingUtils;
import com.fr.swift.util.Util;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
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
    private Iterator<KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>> mergeIt;
    private KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>> current = null;


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
        mergeIt = SortedListMergingUtils.mergeIterator(array2List(iterators),
                this.<KeyValue<Integer, RowTraversal[]>>getComparator());
    }

    protected abstract <V> Comparator<KeyValue<RowIndexKey<T>, V>> getComparator();

    @Override
    public boolean hasNext() {
        return mergeIt.hasNext() || current != null;
    }

    @Override
    public KeyValue<RowIndexKey<T>, List<RowTraversal[]>> next() {
        RowTraversal[][] values = new RowTraversal[iterators.length][];
        RowIndexKey<T> key = current != null ? current.getKey() : null;
        if (current != null) {
            values[current.getValue().getKey()] = current.getValue().getValue();
        }
        // 重置current
        current = null;
        while (mergeIt.hasNext()) {
            KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>> kv = mergeIt.next();
            if (key == null) {
                key = kv.getKey();
                values[kv.getValue().getKey()] = kv.getValue().getValue();
                continue;
            }
            int c = getComparator().compare(new KeyValue<RowIndexKey<T>, Object>(key, null),
                    new KeyValue<RowIndexKey<T>, Object>(kv.getKey(), null));
            if (c == 0) {
                // key相同，收集value
                values[kv.getValue().getKey()] = kv.getValue().getValue();
                continue;
            }
            // key != null且key != kv.getKey()，这个时候返回当前key对应的值，并设置kv为下一次的current
            current = kv;
            break;
        }
        return new KeyValue<RowIndexKey<T>, List<RowTraversal[]>>(key, Arrays.asList(values));
    }

    /**
     * 进行mapper之后的value保持了初始迭代器迭代器数组中的索引（对应哪个segment）
     */
    private List<Iterator<KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>>> array2List(MultiGroupBy<T>[] its) {
        List<Iterator<KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>>> iterators = new ArrayList<Iterator<KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>>>();
        for (int i = 0; i < its.length; i++) {
            iterators.add(new MapperIterator<KeyValue<RowIndexKey<T>, RowTraversal[]>,
                    KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>>(its[i], new KVMapper<T>(i)));
        }
        return iterators;
    }

    private static class KVMapper<T> implements
            Function<KeyValue<RowIndexKey<T>, RowTraversal[]>, KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>> {

        private int iteratorIndex;

        public KVMapper(int iteratorIndex) {
            this.iteratorIndex = iteratorIndex;
        }

        @Override
        public KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>> apply(KeyValue<RowIndexKey<T>, RowTraversal[]> p) {
            return new KeyValue<RowIndexKey<T>, KeyValue<Integer, RowTraversal[]>>(
                    p.getKey(),
                    new KeyValue<Integer, RowTraversal[]>(iteratorIndex, p.getValue())
            );
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
