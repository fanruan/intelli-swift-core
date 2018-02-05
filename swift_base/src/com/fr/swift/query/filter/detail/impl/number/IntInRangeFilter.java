package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class IntInRangeFilter extends AbstractNumberInRangeFilter {

    public IntInRangeFilter(int min, int max, boolean minIncluded,
                            boolean maxIncluded, Column<Integer> column) {
        super(min, max, minIncluded, maxIncluded);
        this.column = column;
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        Integer value = (Integer) data;
        int minValue = min.intValue();
        int maxValue = max.intValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }
}
