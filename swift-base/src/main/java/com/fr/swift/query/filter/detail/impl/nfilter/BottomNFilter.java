package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.query.filter.detail.impl.AbstractDetailFilter;
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
public class BottomNFilter extends AbstractDetailFilter {

    private int bottomN;

    public BottomNFilter(int bottomN, Column column) {
        Util.requireGreaterThanZero(bottomN);
        this.bottomN = bottomN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int endIndex = dict.globalSize() >= bottomN ? bottomN : dict.globalSize();
        // TODO: 2018/3/26 同topN
        return new IntListRowTraversal(
                IntListFactory.createRangeIntList(DictionaryEncodedColumn.NOT_NULL_START_INDEX, endIndex));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex) {
        if (targetIndex == -1) {
            int index = node.getIndex();
            return index < bottomN;
        }
        // TODO: 2018/5/8 依据指标的过滤
        return true;
    }
}
