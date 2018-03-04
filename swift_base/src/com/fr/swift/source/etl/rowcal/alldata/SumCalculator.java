package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.query.aggregator.DoubleAmountAggregateValue;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:12
 */
public class SumCalculator implements AllDataCalculator{

    public static SumCalculator INSTANCE = new SumCalculator();

    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            DoubleAmountAggregateValue value = SumAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(columnKey));
            for (int i = 1; i < segments.length; i++) {
                DoubleAmountAggregateValue otherValue = SumAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(columnKey));
                SumAggregate.INSTANCE.combine(value, otherValue);
            }
            return value.calculate();
        }
        return 0;
    }
}
