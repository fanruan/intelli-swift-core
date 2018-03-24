package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.constant.SwiftGroupByConstants;
import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.iterator.IntListRowTraversal;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.Util;

/**
 * Created by Lyon on 2017/12/4.
 */
public class TopNFilter extends AbstractFilter {

    private int topN;

    public TopNFilter(int topN, Column column) {
        Util.requireGreaterThanZero(topN);
        this.topN = topN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int size = dict.globalSize();
        int startIndex = size >= topN ? size - topN : SwiftGroupByConstants.DICTIONARY.NOT_NULL_START_INDEX;
        return new IntListRowTraversal(IntListFactory.createRangeIntList(startIndex, dict.size() - 1));
    }

    @Override
    public boolean matches(SwiftNode node) {
        int index = node.getIndex();
        return index < topN;
    }
}
