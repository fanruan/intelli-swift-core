package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.DoubleContainFilter;
import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class DoubleNotContainFilter extends AbstractNotOperatorFilter {

    public DoubleNotContainFilter(int rowCount, Set<Double> notInGroups, Column<Double> column) {
        super(rowCount, new DoubleContainFilter(notInGroups, column));
    }
}
