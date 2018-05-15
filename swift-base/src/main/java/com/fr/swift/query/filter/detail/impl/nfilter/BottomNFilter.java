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
 * 取字典排序最小的N个
 * Created by Lyon on 2017/12/4.
 */
public class BottomNFilter extends AbstractNFilter {

    private int bottomN;

    public BottomNFilter(int bottomN, Column column) {
        Util.requireGreaterThanZero(bottomN);
        this.bottomN = bottomN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int globalSize = dict.globalSize();
        int globalEnd = globalSize > bottomN ? bottomN : globalSize - 1;
        int localStart = DictionaryEncodedColumn.NOT_NULL_START_INDEX;
        int localEnd = getLocalIndex(dict, localStart, dict.size() - 1, globalEnd);
        // TODO: 2018/3/26 同topN
        return new IntListRowTraversal(
                IntListFactory.createRangeIntList(localStart, localEnd));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        if (targetIndex == -1) {
            int index = node.getIndex();
            return index < bottomN;
        } else {
            Double value = getValue(node, targetIndex);
            return node.getAggregatorValue(targetIndex).calculate() <= value;
        }
    }

    @Override
    public NTree<Double> getNTree() {
        return new NTree<Double>(Comparators.<Double>asc(), bottomN);
    }
}
