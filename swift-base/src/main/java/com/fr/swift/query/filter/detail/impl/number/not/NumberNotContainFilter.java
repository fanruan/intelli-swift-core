package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberContainFilter;
import com.fr.swift.segment.column.Column;

import java.util.Set;

/**
 * Created by Lyon on 2017/11/28.
 */
public class NumberNotContainFilter extends AbstractNotOperatorFilter {

    public NumberNotContainFilter(int rowCount, Set<Double> notInGroups, Column<Number> column) {
        super(rowCount, new NumberContainFilter(notInGroups, column));
    }
}
