package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.constant.SwiftConstants;
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
public class BottomNFilter extends AbstractFilter {

    private int bottomN;

    public BottomNFilter(int bottomN, Column column) {
        Util.requireGreaterThanZero(bottomN);
        this.bottomN = bottomN;
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        int endIndex = dict.globalSize() >= bottomN ? bottomN : dict.globalSize();
        return new IntListRowTraversal(
                IntListFactory.createRangeIntList(SwiftConstants.DICTIONARY.NOT_NULL_START_INDEX, endIndex));
    }

    @Override
    public boolean matches(SwiftNode node) {
        int index = node.getIndex();
        int size = node.getParent().getChildrenSize();
        return size - index < bottomN;
    }
}
