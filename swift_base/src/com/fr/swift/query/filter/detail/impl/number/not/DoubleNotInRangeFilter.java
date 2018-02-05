package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.DoubleInRangeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class DoubleNotInRangeFilter extends AbstractNotOperatorFilter {

    public DoubleNotInRangeFilter(int rowCount, double min, double max, boolean minIncluded,
                                  boolean maxIncluded, Column<Double> column) {
        super(rowCount, new DoubleInRangeFilter(min, max, minIncluded, maxIncluded, column));
    }
}
