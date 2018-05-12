package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.compare.Comparators;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Util;

/**
 * 取字典排序中最大的N个
 * Created by Lyon on 2017/12/4.
 */
public class TopNFilter extends AbstractNFilter {

    private int topN;

    public TopNFilter(int topN, Column column) {
        Util.requireGreaterThanZero(topN);
        this.topN = topN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int size = dict.globalSize();
        int startIndex = size >= topN ? size - topN : DictionaryEncodedColumn.NOT_NULL_START_INDEX;
        // TODO: 2018/3/26 当前只能保证单块计算准确。要根据是否有全局字典来决定如何计算topN
        return new IntListRowTraversal(IntListFactory.createRangeIntList(startIndex, dict.size() - 1));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        // 对接点本身的前几个过滤
        if (targetIndex == -1) {
            int index = node.getIndex();
            int size = node.getParent().getChildrenSize();
            return (size - index) < topN;
        } else {
            Double value = getValue(node, targetIndex);
            return node.getAggregatorValue(targetIndex).calculate() >= value;
        }
    }

    @Override
    public NTree<Double> getNTree() {
        return new NTree<Double>(Comparators.<Double>desc(), topN);
    }
}
