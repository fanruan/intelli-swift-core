package com.fr.swift.query.group.by;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by pony on 2018/3/29.
 * 返回多个segment合并之后的结果，按传入的segment的multigroupby的顺序返回
 */
public abstract class MergerGroupBy<T> implements Iterator<KeyValue<RowIndexKey<T>, List<RowTraversal[]>>> {
    protected MultiGroupBy[] iterators;
    private KeyValue<RowIndexKey<T>, List<RowTraversal[]>> next;
    private TreeMap<RowIndexKey<T>, List<RowTraversal[]>> mergeMap;

    public MergerGroupBy(MultiGroupBy[] iterators) {
        Util.requireNonNull(iterators);
        this.iterators = iterators;
    }

    //初始化，把每个迭代器的第一个放入map
    protected void initMap() {
        mergeMap = new TreeMap<RowIndexKey<T>, List<RowTraversal[]>>(getComparator());
        for (int i = 0; i < iterators.length; i++) {
            pick(i);
        }
        moveNext();
    }

    protected abstract Comparator<RowIndexKey<T>> getComparator();

    @Override
    public boolean hasNext() {
        return next != null;
    }

    //只有被取出的才需要put进来
    private void moveNext(List<RowTraversal[]> value) {
        for (int i = 0; i < value.size(); i++) {
            if (value.get(i) == null) {
                continue;
            }
            pick(i);
        }
    }

    //把第N个的迭代器的值加入到treemap里
    private void pick(int index) {
        if (iterators[index].hasNext()) {
            KeyValue<RowIndexKey<T>, RowTraversal[]> kv = iterators[index].next();
            List<RowTraversal[]> values = mergeMap.get(kv.getKey());
            if (values != null) {
                values.set(index, kv.getValue().clone());
            } else {
                values = new ArrayList<RowTraversal[]>(Arrays.asList(new RowTraversal[iterators.length][]));
                values.set(index, kv.getValue().clone());
                mergeMap.put(kv.getKey(), values);
            }
        }
    }


    @Override
    public KeyValue<RowIndexKey<T>, List<RowTraversal[]>> next() {
        KeyValue<RowIndexKey<T>, List<RowTraversal[]>> temp = next;
        moveNext();
        return temp;
    }

    protected void moveNext() {
        Map.Entry<RowIndexKey<T>, List<RowTraversal[]>> entry = mergeMap.pollFirstEntry();
        if (entry != null) {
            moveNext(entry.getValue());
            next = new KeyValue<RowIndexKey<T>, List<RowTraversal[]>>(entry.getKey(), entry.getValue());
        } else {
            next = null;
        }
    }


    @Override
    public void remove() {

    }
}
