package com.fr.swift.query.filter.detail.impl.number;

import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.column.Column;

/**
 * Created by Lyon on 2017/11/28.
 */
public class DoubleInRangeFilter extends AbstractNumberInRangeFilter {

    public DoubleInRangeFilter(double min, double max, boolean minIncluded,
                               boolean maxIncluded, Column<Double> column) {
        super(min, max, minIncluded, maxIncluded);
        this.column = column;
    }

    @Override
    public boolean matches(SwiftNode node) {
        Object data = node.getData();
        if (data == null) {
            return false;
        }
        Double value = (Double) data;
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }
}
