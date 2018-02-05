package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class LongContainFilter extends AbstractNumberContainFilter {

    public LongContainFilter(Set<Long> groups, Column<Long> column) {
        super(groups);
        this.column = column;
    }

    @Override
    protected Number getNodeData(Number data) {
        return data.longValue();
    }
}
