package com.fr.swift.query.aggregator;

import java.util.Iterator;

/**
 * 笛卡尔积合并
 *
 * @author yee
 * @date 2019-06-26
 */
public class CartesianAggregatorCombiner extends BaseAggregatorCombiner {

    public CartesianAggregatorCombiner(int size) {
        super(size);
    }

    @Override
    public Iterator<AggregatorValue[]> getCombineIterator() {
        return new CartesianCombineIterator(getAggregatorValue());
    }

    private class CartesianCombineIterator implements Iterator<AggregatorValue[]> {
        private Iterator<AggregatorValue>[] iterators;
        private AggregatorValue[] source;
        private AggregatorValue[] tmp;
        private int lastIterator = -1;
        private int preLastIterator = -1;


        public CartesianCombineIterator(final AggregatorValue[] source) {

            this.source = source;
            this.tmp = new AggregatorValue[source.length];
            init();
        }

        private void init() {
            iterators = new Iterator[source.length];
            for (int i = 0; i < source.length; i++) {
                if (source[i] instanceof IterableAggregatorValue) {
                    iterators[i] = ((IterableAggregatorValue) source[i]).iterator();
                    lastIterator = i;
                }
            }
        }

        @Override
        public boolean hasNext() {
            for (Iterator iterator : iterators) {
                if (null != iterator && iterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public AggregatorValue[] next() {
            for (int i = tmp.length - 1; i >= 0; i--) {
                if (iterators[i] == null) {
                    tmp[i] = source[i];
                    continue;
                }
                if (lastIterator == -1) {
                    lastIterator = i;
                }
                if (iterators[i].hasNext()) {
                    if (tmp[i] == null) {
                        tmp[i] = iterators[i].next();
                    } else if (i == lastIterator) {
                        tmp[i] = iterators[i].next();
                        if (preLastIterator != -1) {
                            lastIterator = preLastIterator;
                        }
                    }
                } else if (i == lastIterator) {
                    iterators[i] = ((IterableAggregatorValue) source[i]).iterator();
                    tmp[i] = iterators[i].next();
                    if (preLastIterator == -1) {
                        preLastIterator = lastIterator;
                    }
                    lastIterator = -1;
                }

            }
            return tmp;
        }

        @Override
        public void remove() {

        }
    }
}
