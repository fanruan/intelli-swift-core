package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.query.filter.detail.impl.AbstractFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
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
        int endIndex = size >= topN ? size - topN - 1 : 0;
        return new BottomNFilterRowTraversal(endIndex, dict);
    }

    @Override
    public boolean matches(SwiftNode node) {
        int index = node.getIndex();
        return index < topN;
    }
}
