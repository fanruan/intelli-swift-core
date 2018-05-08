package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.NumberInRangeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class NumberNotInRangeFilter extends AbstractNotOperatorFilter {

    public NumberNotInRangeFilter(int rowCount, Double min, Double max, boolean minIncluded,
                                  boolean maxIncluded, Column<Number> column) {
        super(rowCount, new NumberInRangeFilter(min, max, minIncluded, maxIncluded, column));
    }
}
