package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.IntInRangeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class IntNotInRangeFilter extends AbstractNotOperatorFilter {

    public IntNotInRangeFilter(int rowCount, int min, int max, boolean minIncluded,
                               boolean maxIncluded, Column<Integer> column) {
        super(rowCount, new IntInRangeFilter(min, max, minIncluded, maxIncluded, column));
    }
}
