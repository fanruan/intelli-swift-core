package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;

/**
 * Created by Lyon on 2018/7/3.
 */
public class NotFilter implements DetailFilter {

    private int rowCount;
    private DetailFilter filter;

    public NotFilter(int rowCount, DetailFilter filter) {
        this.rowCount = rowCount;
        this.filter = filter;
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        return filter.createFilterIndex().getNot(rowCount);
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        return node.getData() != null && !filter.matches(node, targetIndex, converter);
    }
}
