package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.filter.match.MatchConverter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/12/5.
 */
public class NotNullFilter extends AbstractNotOperatorFilter {

    public NotNullFilter(int rowCount, Column column) {
        super(rowCount, new NullFilter(column));
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        if (targetIndex == -1){
            return node.getData() != null;
        }
        return node.getAggregatorValue(targetIndex).calculateValue() != null;
    }
}
