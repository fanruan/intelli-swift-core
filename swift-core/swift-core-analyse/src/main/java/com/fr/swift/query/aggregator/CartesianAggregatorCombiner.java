package com.fr.swift.query.aggregator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
    public Iterator<SwiftNode> getSwiftNodeIterator(int depth) {
        return new CartesianCombineIterator(depth, getAggregatorValue());
    }

    private class CartesianCombineIterator implements Iterator<SwiftNode> {
        private int depth;
        private Iterator<AggregatorValue>[] iterators;
        private AggregatorValue[] source;
        private AggregatorValue[] tmp;
        private int lastIterator = -1;
        private int preLastIterator = -1;


        public CartesianCombineIterator(int depth, AggregatorValue[] source) {
            this.depth = depth;
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
        public SwiftNode next() {
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
            return calculateAggregatorNode();
        }

        private SwiftNode calculateAggregatorNode() {
            List<AggregatorValue> list = new ArrayList<AggregatorValue>();
            SwiftNode swiftNode = null;
            SwiftNode leafNode = null;
            for (AggregatorValue value : tmp) {
                if (value instanceof SwiftNodeNodeAggregatorValue) {
                    SwiftNodeNodeAggregatorValue groupValue = (SwiftNodeNodeAggregatorValue) value;
                    swiftNode = groupValue.calculateValue(depth);
                    leafNode = groupValue.getLeafNode();
                    list.addAll(Arrays.asList(leafNode.getAggregatorValue()));
                } else {
                    list.add(value);
                }
            }
            if (null == swiftNode) {
                swiftNode = new GroupNode(depth + 1, null);
                swiftNode.setAggregatorValue(list.toArray(new AggregatorValue[0]));
            } else {
                leafNode.setAggregatorValue(list.toArray(new AggregatorValue[0]));
            }
            return swiftNode;
        }

        @Override
        public void remove() {

        }
    }
}
