package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.DoubleAmountAggregateValue;
import com.fr.swift.query.aggregator.MaxAggregate;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:45
 */
public class MaxCalculator implements AllDataCalculator {

    public static MaxCalculator INSTANCE = new MaxCalculator();

    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            DoubleAmountAggregateValue maxValue = MaxAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(columnKey));
            for (int i = 1; i < segments.length; i++) {
                DoubleAmountAggregateValue otherValue = MaxAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(columnKey));
                MaxAggregate.INSTANCE.combine(maxValue, otherValue);
            }
            return maxValue.calculate();
        }
        return 0;
    }
}
