package com.fr.swift.query.filter.detail.impl.number.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.number.LongInRangeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class LongNotInRangeFilter extends AbstractNotOperatorFilter {

    public LongNotInRangeFilter(int rowCount, long min, long max, boolean minIncluded,
                                boolean maxIncluded, Column<Long> column) {
        super(rowCount, new LongInRangeFilter(min, max, minIncluded, maxIncluded, column));
    }
}
