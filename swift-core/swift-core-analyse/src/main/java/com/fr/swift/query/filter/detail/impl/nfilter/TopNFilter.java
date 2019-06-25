package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Assert;

import java.util.Iterator;

/**
 * 取字典排序中最大的N个
 * Created by Lyon on 2017/12/4.
 */
public class TopNFilter extends AbstractNFilter {

    private int topN;

    public TopNFilter(int topN, Column column) {
        Assert.isTrue(topN > 0);
        this.topN = topN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int globalSize = dict.globalSize();
        int globalStart = globalSize > topN ? globalSize - topN : DictionaryEncodedColumn.NOT_NULL_START_INDEX;
        int localEnd = dict.size() - 1;
        int localStart = getLocalIndex(dict, DictionaryEncodedColumn.NOT_NULL_START_INDEX, localEnd, globalStart);
        return new IntListRowTraversal(IntListFactory.createRangeIntList(localStart, localEnd));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        // 对接点本身的前几个过滤
        if (targetIndex == -1) {
            int index = node.getIndex();
            int size = node.getParent().getChildrenSize();
            return (size - index) <= topN;
        } else {
            Double value = getValue(node, targetIndex);
            Iterator<AggregatorValueRow> iterator = node.getAggregatorValue().iterator();
            boolean matches = false;
            while (iterator.hasNext()) {
                AggregatorValueRow next = iterator.next();
                boolean match = ((AggregatorValue<Double>) next.getValue(targetIndex)).calculateValue() >= value;
                matches |= match;
                if (!match) {
                    iterator.remove();
                }
            }
            return matches;
        }
    }

    @Override
    public NTree<Double> getNTree() {
        return new NTree<Double>(Comparators.<Double>desc(), topN);
    }
}
