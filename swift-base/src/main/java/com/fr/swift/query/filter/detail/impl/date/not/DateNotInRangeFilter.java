package com.fr.swift.query.filter.detail.impl.date.not;

import com.fr.swift.query.filter.detail.impl.AbstractNotOperatorFilter;
import com.fr.swift.query.filter.detail.impl.date.DateInRangeFilter;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/29.
 */
public class DateNotInRangeFilter extends AbstractNotOperatorFilter {

    public DateNotInRangeFilter(int rowCount, Long start, Long end, Column<Long> column) {
        super(rowCount, new DateInRangeFilter(start, end, column));
    }
}
