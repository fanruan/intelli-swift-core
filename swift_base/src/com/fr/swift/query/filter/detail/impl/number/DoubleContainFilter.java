package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class DoubleContainFilter extends AbstractNumberContainFilter {

    public DoubleContainFilter(Set<Double> groups, Column<Double> column) {
        super(groups);
        this.column = column;
    }

    @Override
    protected Number getNodeData(Number data) {
        return data.doubleValue();
    }
}
