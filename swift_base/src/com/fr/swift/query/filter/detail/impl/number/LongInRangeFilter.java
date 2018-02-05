package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class LongInRangeFilter extends AbstractNumberInRangeFilter {

    public LongInRangeFilter(long min, long max, boolean minIncluded, boolean maxIncluded, Column<Long> column) {
        super(min, max, minIncluded, maxIncluded);
        this.column = column;
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        Long value = (Long) data;
        long minValue = min.longValue();
        long maxValue = max.longValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }
}
