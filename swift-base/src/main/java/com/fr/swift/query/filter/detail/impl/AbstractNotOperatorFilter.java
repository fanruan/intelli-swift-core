package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;

/**
 * Created by Lyon on 2017/11/27.
 */
public abstract class AbstractNotOperatorFilter implements DetailFilter {

    private int rowCount;

    private DetailFilter filter;

    public AbstractNotOperatorFilter(int rowCount, DetailFilter filter) {
        this.rowCount = rowCount;
        this.filter = filter;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        return filter.createFilterIndex().getNot(rowCount);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        return node.getData() == null ? false : !filter.matches(node, targetIndex, converter);
    }

}
