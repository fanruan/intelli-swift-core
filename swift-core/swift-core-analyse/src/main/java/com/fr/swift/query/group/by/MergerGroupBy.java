package com.fr.swift.query.group.by;

import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.queue.SortedListMergingUtils;
import com.fr.swift.util.Util;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/3/29.
 * 返回多个segment合并之后的结果，按传入的segment的MultiGroupBy的顺序返回
 */
public abstract class MergerGroupBy<T> implements Iterator<Pair<T, List<RowTraversal[]>>> {

    //升序还是降序
    protected final boolean[] asc;
    private MultiGroupBy<T>[] iterators;
    private Iterator<Pair<T, List<RowTraversal[]>>> mergeIt;

    public MergerGroupBy(MultiGroupBy<T>[] iterators, boolean[] asc) {
        Util.requireNonNull(iterators);
        Util.requireNonNull(asc);
        this.iterators = iterators;
        this.asc = asc;
    }

    protected abstract Comparator<Pair<T, List<RowTraversal[]>>> getComparator();

    /**
     * 初始化迭代器，给子类调用
     */
    protected void init() {
        mergeIt = SortedListMergingUtils.mergeIterator(mapIterator(),
                this.getComparator(), new PairCombiner<T>());
    }

    private static class PairCombiner<T> implements Combiner<Pair<T, List<RowTraversal[]>>> {

        @Override
        public void combine(Pair<T, List<RowTraversal[]>> current, Pair<T, List<RowTraversal[]>> other) {
            if (current.getValue().size() <= 1) {
                List<RowTraversal[]> list = new ArrayList<RowTraversal[]>();
                list.addAll(current.getValue());
                list.addAll(other.getValue());
                current.setValue(list);
            } else {
                current.getValue().addAll(other.getValue());
            }
        }
    }

    private List<Iterator<Pair<T, List<RowTraversal[]>>>> mapIterator() {
        List<Iterator<Pair<T, List<RowTraversal[]>>>> list = new ArrayList<Iterator<Pair<T, List<RowTraversal[]>>>>();
        Function<Pair<T, RowTraversal[]>, Pair<T, List<RowTraversal[]>>> fn = new Function<Pair<T, RowTraversal[]>, Pair<T, List<RowTraversal[]>>>() {
            @Override
            public Pair<T, List<RowTraversal[]>> apply(Pair<T, RowTraversal[]> p) {
                return Pair.of(p.getKey(), Collections.singletonList(p.getValue()));
            }
        };
        for (MultiGroupBy<T> it : iterators) {
            list.add(new MapperIterator(it, fn));
        }
        return list;
    }

    @Override
    public boolean hasNext() {
        return mergeIt.hasNext();
    }

    @Override
    public Pair<T, List<RowTraversal[]>> next() {
        return mergeIt.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
