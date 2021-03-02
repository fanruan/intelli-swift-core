package com.fr.swift.cloud.query.group.by;

import com.fr.swift.cloud.query.aggregator.Combiner;
import com.fr.swift.cloud.structure.Pair;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import com.fr.swift.cloud.structure.queue.SortedListMergingUtils;
import com.fr.swift.cloud.util.Util;
import com.fr.swift.cloud.util.function.Function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2018/3/29.
 * 返回多个segment合并之后的结果，按传入的segment的MultiGroupBy的顺序返回
 */
public abstract class MergerGroupBy<T> implements Iterator<Pair<T, RowTraversal[][]>> {

    //升序还是降序
    protected final boolean[] asc;
    private MultiGroupBy<T>[] iterators;
    private Iterator<Item<T>> mergeIt;

    public MergerGroupBy(MultiGroupBy<T>[] iterators, boolean[] asc) {
        Util.requireNonNull(iterators);
        Util.requireNonNull(asc);
        this.iterators = iterators;
        this.asc = asc;
    }

    protected abstract Comparator<Item<T>> getComparator();

    /**
     * 初始化迭代器，给子类调用
     */
    protected void init() {
        mergeIt = SortedListMergingUtils.mergeIterator(mapIterator(),
                this.getComparator(), new PairCombiner<T>());
    }

    private static class PairCombiner<T> implements Combiner<Item<T>>, Serializable {
        private static final long serialVersionUID = 2327585136570426873L;

        @Override
        public void combine(Item<T> current, Item<T> other) {
            current.getPair().getValue()[other.getIndex()] = other.getPair().getValue()[other.getIndex()];
        }
    }

    private List<Iterator<Item<T>>> mapIterator() {
        List<Iterator<Item<T>>> iteratorList = new ArrayList<Iterator<Item<T>>>();
        final int len = iterators.length;
        for (int i = 0; i < iterators.length; i++) {
            Iterator<Pair<T, RowTraversal[]>> iterator = iterators[i];
            final int finalI = i;
            Function<Pair<T, RowTraversal[]>, Item<T>> fn = new Function<Pair<T, RowTraversal[]>, Item<T>>() {
                @Override
                public Item<T> apply(Pair<T, RowTraversal[]> p) {
                    RowTraversal[][] traversals = new RowTraversal[len][];
                    traversals[finalI] = p.getValue();
                    return new Item<T>(finalI, Pair.of(p.getKey(), traversals));
                }
            };
            iteratorList.add(new MapperIterator<Pair<T, RowTraversal[]>, Item<T>>(iterator, fn));
        }
        return iteratorList;
    }

    @Override
    public boolean hasNext() {
        return mergeIt.hasNext();
    }

    @Override
    public Pair<T, RowTraversal[][]> next() {
        return mergeIt.next().getPair();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    static class Item<T> {
        private int index;
        private Pair<T, RowTraversal[][]> pair;

        public Item(int index, Pair<T, RowTraversal[][]> pair) {
            this.index = index;
            this.pair = pair;
        }

        public int getIndex() {
            return index;
        }

        public Pair<T, RowTraversal[][]> getPair() {
            return pair;
        }
    }
}
