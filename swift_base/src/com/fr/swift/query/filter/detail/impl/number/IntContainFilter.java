package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class IntContainFilter extends AbstractNumberContainFilter {

    public IntContainFilter(Set<Integer> groups, Column<Integer> column) {
        super(groups);
        this.column = column;
    }

    @Override
    protected Number getNodeData(Number data) {
        return data.intValue();
    }
}
